package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.annotation.MainCompetitionPolicy;
import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.dto.CompetitionDto;
import PlayMakers.SportsIT.dto.CompetitionFormDto;
import PlayMakers.SportsIT.dto.CompetitionResultDto;
import PlayMakers.SportsIT.exceptions.ErrorCode;
import PlayMakers.SportsIT.exceptions.*;
import PlayMakers.SportsIT.exceptions.competition.IllegalMemberTypeException;
import PlayMakers.SportsIT.repository.CategoryRepository;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CompetitionService {
    private final CompetitionRepository competitionRepository;
    private final MemberRepository memberRepository;
    private final @MainCompetitionPolicy CompetitionPolicy competitionPolicy;
    private final CategoryRepository categoryRepository;

    public Competition create(CompetitionDto dto) {
        log.info("대회 생성 요청: {}", dto);
        // host가 존재하지 않으면 예외 발생
        Member host = dto.getHost();

        // host의 memberType이 ROLE_INSTITUTION 또는 ROLE_ADMIN이 아니면 예외 발생
        if (host.getMemberType().stream().noneMatch(memberType ->
                        memberType.getRoleName().equals("ROLE_INSTITUTION") ||
                        memberType.getRoleName().equals("ROLE_ADMIN"))) {
            throw new UnAuthorizedException(ErrorCode.NOT_HOST, "대회 주최 권한이 없습니다.");
        }

        Competition newCompetition = dto.toEntity();
        Set<Category> categories = new HashSet<>();
        if (dto.getCategories() == null) {
            dto.setCategories(new ArrayList<>(){{add("ETC");}});
        }
        for (String categoryId : dto.getCategories()) {
            categories.add(categoryRepository.findById(categoryId).orElseThrow(
                    () -> new EntityNotFoundException("해당 카테고리가 존재하지 않습니다.")));
        }
        newCompetition.setCategories(categories);
        newCompetition.setViewCount(0);
        newCompetition.setScrapCount(0);

        // 시간 정보가 유효하지 않으면 예외 발생
        checkTimeValidity(newCompetition);

        // 대회 상태 설정 (PLANNING, RECRUITING, RECRUITING_END, IN_PROGRESS) : competition.state == null 일 경우
        if (newCompetition.getState() == null) {
            newCompetition.setState(competitionPolicy.getCompetitionState(newCompetition)); // 일정정보가 비정상적이면 IllegalArgumentException 발생
        }
        // 대회 타입 설정 (FREE, PREMIUM, VIP) : competition.type == null 일 경우
        if (newCompetition.getCompetitionType() == null) {
            newCompetition.setCompetitionType(competitionPolicy.getCompetitionType(host));
        }

        // 필수 정보가 없으면 예외 발생
        checkRequiredInfo(newCompetition);

        log.info("대회 생성 완료: {}", newCompetition);

        return competitionRepository.save(newCompetition);
    }
    public Competition findById(Long competitionId) {
        log.info("대회 조회 요청: {}", competitionId);
        Competition competition = competitionRepository.findById(competitionId).orElseThrow(() -> new PlayMakers.SportsIT.exceptions.EntityNotFoundException(
                ErrorCode.COMPETITION_NOT_FOUND, "대회 ID: " + competitionId));
        competition.setViewCount(competition.getViewCount() + 1);
        return competition;
    }
    public List<Competition> findAll(){
        log.info("대회 전체 조회 요청");
        return competitionRepository.findAll();
    }

    public Competition update(Long competitionId, CompetitionDto.Form dto) {
        log.info("대회 수정 : {}", competitionId);

        // 수정 사항 검증
        Competition updated = dto.toAllArgsDto().toEntity();
        checkTimeValidity(updated); // 일정정보가 비정상적이면 IllegalArgumentException 발생

        // 대회 찾기
        Competition competition = competitionRepository.findById(competitionId).orElseThrow(
                () -> new PlayMakers.SportsIT.exceptions.EntityNotFoundException(
                        ErrorCode.COMPETITION_NOT_FOUND,
                        "대회 ID: " + competitionId));

        // 수정 사항 적용
        updateCompetition(competition, dto.toAllArgsDto());

        return competitionRepository.save(competition);
    }

    public void delete(Long competitionId) {
        log.info("CompetitionService.delete() : {}", competitionId);
        // 관계를 맺은 table 삭제

        competitionRepository.deleteById(competitionId);
    }

    public Slice<Competition> getCompetitionSlice(String keyword,
                                                  List<String> filteringConditions,
                                                  String orderBy,
                                                  int page, int size) {
        log.info("대회 목록 조회 요청: {}", keyword);

        Pageable pageable = getPageableProperties(orderBy, page, size);

        Slice<Competition> competitions = competitionRepository.findCompetitionBySlice(keyword, filteringConditions, pageable);

        return competitions;
    }
    public Slice<Competition> getCompetitionSliceByHostId(Long hostId,
                                                          int page, int size) {
        log.info("주최자 기준 대회 목록 조회 요청: {}", hostId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Slice<Competition> competitions = competitionRepository.findCompetitionsBySliceWithHostUid(hostId, pageable);

        return competitions;
    }

    @NotNull
    private static Pageable getPageableProperties(String orderBy, int page, int size) {
        Pageable pageable;
        if(orderBy != null && !orderBy.isEmpty()) pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, orderBy));
        else pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        return pageable;
    }

    private static void updateCompetition(Competition competition, CompetitionDto dto) {
        competition.setName(dto.getName());
        competition.setCategory(dto.getSportCategory());
        competition.setRecruitingStart(dto.getRecruitingStart());
        competition.setRecruitingEnd(dto.getRecruitingEnd());
        competition.setStartDate(dto.getStartDate());
        competition.setEndDate(dto.getEndDate());
        competition.setTotalPrize(dto.getTotalPrize());
        competition.setContent(dto.getContent());
        competition.setLocation(dto.getLocation());
        competition.setLocationDetail(dto.getLocationDetail());
        competition.setMaxPlayer(dto.getMaxPlayer());
        competition.setMaxViewer(dto.getMaxViewer());
        //competition.setPosters(dto.getPosters());  // 변경하면 참조가 깨져버림
        //competition.setAgreements(dto.getAgreements());  // 변경하면 참조가 깨져버림
    }

    public List<CompetitionResultDto> getAllResultsByCompetition(Long competitionId) {
        Competition competition = competitionRepository.findByCompetitionId(competitionId);
        List<CompetitionResult> competitionResults = competition.getCompetitionResults();
        List<CompetitionResultDto> competitionResultDtos = new ArrayList<>();
        for(CompetitionResult cr : competitionResults){
            CompetitionResultDto dto = CompetitionResultDto.builder()
                    .content(cr.getContent())
                    .name(cr.getMember().getName())
                    .historyDate(cr.getHistoryDate())
                    .build();
            competitionResultDtos.add(dto);
        }
        return competitionResultDtos;
    }


    /*
    *   대회 생성/수정 시 필수 정보 체크
     */

    private static void checkRequiredInfo(Competition newCompetition) {
        String reason = "";
        if (newCompetition.getName() == null) reason += "\n대회 이름";
        if (newCompetition.getHost() == null) reason += "\n대회 주최자";
        if (newCompetition.getCategory() == null) reason += "\n대회 종목";
        if (newCompetition.getContent() == null) reason += "\n대회 내용";
        if (newCompetition.getLocation() == null) reason += "\n대회 장소";
        if (newCompetition.getLocationDetail() == null) reason += "\n대회 장소 상세";
        if (newCompetition.getStartDate() == null) reason += "\n대회 시작일";
        if (newCompetition.getEndDate() == null) reason += "\n대회 종료일";
        if (newCompetition.getRecruitingStart() == null) reason += "\n대회 모집 시작일";
        if (newCompetition.getRecruitingEnd() == null) reason += "\n대회 모집 종료일";
        if (newCompetition.getCompetitionType() == null) reason += "\n대회 프리미엄";
        if (reason.length() > 0) {
            reason = "대회 필수 정보가 없습니다." + reason;
            throw new InvalidValueException(ErrorCode.INVALID_COMPETITION_PARAMETER, reason);
        }
    }
    private static void checkTimeValidity(Competition competition) {
        String reason = "";
        if (competition.getEndDate().isBefore(competition.getStartDate())) {
            reason += "대회 종료일이 대회 시작일보다 빠릅니다. ";
        }
        if (competition.getStartDate().isBefore(competition.getRecruitingEnd())) {
            reason += "대회 시작일이 대회 모집 종료일보다 빠릅니다. ";
        }
        if (competition.getRecruitingEnd().isBefore(competition.getRecruitingStart())) {
            reason += "대회 모집 종료일이 대회 모집 시작일보다 빠릅니다. ";
        }
        if(!reason.isEmpty()) throw new InvalidValueException(ErrorCode.INVALID_COMPETITION_PARAMETER, reason);
    }

    public Long calculatePrice(CompetitionTemplate template, CompetitionFormDto formDto) {
        Long amount = 0L;
        Map<String, Map> sectorPriceMap = new HashMap<>();
        for (CompetitionTemplate.Sector sector : template.getSectors()) {
            Map<String, Long> priceMap = new HashMap<>() {{
                put("cost", sector.getCost());
                put("expandCost", sector.getExpandCost());
            }};
            sectorPriceMap.put(sector.getTitle(), priceMap);
        }
        Map<String, List<Map>> sectorSubSectorMap = new HashMap<>();
        for (CompetitionForm.Sector sector : formDto.getSectors()) {
            for (CompetitionForm.SubSector subSector : sector.getSubSectors()) {
                if(subSector.isChecked()) {
                    // sectorSubSectorMap에 sector가 없다면 sector.getTitle을 추가
                    if(!sectorSubSectorMap.containsKey(sector.getTitle())) {
                        try {
                            sectorPriceMap.get(sector.getTitle());
                        } catch (NullPointerException e) {
                            throw new IllegalArgumentException("선택한 부문이 템플릿에 존재하지 않습니다.");
                        }
                        Map<String, Long> priceMap = sectorPriceMap.get(sector.getTitle());
                        log.info("priceMap: {}", priceMap);
                        Long cost = priceMap.get("cost");
                        sectorSubSectorMap.put(sector.getTitle(), new ArrayList<>() {{
                            add(new HashMap<>() {{
                                put(subSector.getName(), cost);
                            }});
                        }});
                        amount += cost;
                    } else {
                        // sectorSubSectorMap에 sector가 있다면 sector에 subSector 추가
                        Map<String, Long> priceMap = sectorPriceMap.get(sector.getTitle());
                        Long cost = priceMap.get("expandCost");
                        sectorSubSectorMap.get(sector.getTitle()).add(new HashMap<>() {{
                            put(subSector.getName(), cost);
                        }});
                        amount += cost;
                    }

                }
            }
        }

        Long vat = formDto.getVat();
        Long fee = formDto.getFee();
        Long insurance = formDto.getInsurance();
        amount += vat + fee + insurance;

        return amount;
    }

    public Object getCategoriesByCompetition(Long competitionId) {
        Competition competition = competitionRepository.findByCompetitionId(competitionId);
        return competition.getCategories();

    }

    // 대회가 수정 가능한지 체크
    public void checkCompetitionNotStarted(Competition competition) {
        if(competition.getEndDate().isBefore(LocalDateTime.now())) {
            throw new RequestDeniedException(ErrorCode.COMPETITION_NOT_AVAILABLE, "대회가 종료되었습니다. 관리자에게 문의해주세요.");
        }
        else if(competition.getStartDate().isBefore(LocalDateTime.now())) {
            throw new RequestDeniedException(ErrorCode.COMPETITION_NOT_AVAILABLE, "대회가 시작되었습니다. 관리자에게 문의해주세요.");
        }
    }
}
