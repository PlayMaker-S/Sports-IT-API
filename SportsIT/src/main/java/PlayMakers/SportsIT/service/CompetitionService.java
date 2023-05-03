package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.annotation.MainCompetitionPolicy;
import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.dto.CompetitionDto;
import PlayMakers.SportsIT.exceptions.competition.IllegalMemberTypeException;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CompetitionService {
    private final CompetitionRepository competitionRepository;
    private final MemberRepository memberRepository;
    private final @MainCompetitionPolicy CompetitionPolicy competitionPolicy;

    public Competition create(CompetitionDto dto) {
        log.info("대회 생성 요청: {}", dto);
        // host가 존재하지 않으면 예외 발생
        Member host = memberRepository.findById(dto.getHost().getUid()).orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));

        // host의 memberType이 ROLE_INSTITUTION 또는 ROLE_ADMIN이 아니면 예외 발생
        if (!host.getMemberType().stream().anyMatch(memberType ->
                        memberType.getRoleName().equals("ROLE_INSTITUTION") ||
                        memberType.getRoleName().equals("ROLE_ADMIN"))) {
            throw new IllegalMemberTypeException("대회 생성 권한이 없습니다.");
        }

        Competition newCompetition = dto.toEntity();
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

    public Competition update(Long competitionId, CompetitionDto dto) {
        log.info("대회 수정 : {}", competitionId);

        // 수정 사항 검증
        Competition updated = dto.toEntity();
        checkTimeValidity(updated); // 일정정보가 비정상적이면 IllegalArgumentException 발생

        // 대회 찾기
        Competition competition = competitionRepository.findById(
                competitionId).orElseThrow(() -> new EntityNotFoundException("해당 대회가 존재하지 않습니다."));

        // 수정 사항 적용
        updateCompetition(competition, dto);

        return competitionRepository.save(competition);
    }

    public void delete(Long competitionId) {
        log.info("CompetitionService.delete() : {}", competitionId);
        // 관계를 맺은 table 삭제



        competitionRepository.deleteById(competitionId);
    }

    public Slice<Competition> getCompetitionSlice(String keyword, Pageable pageable) {
        log.info("대회 목록 조회 요청: {}", keyword);
        Slice<Competition> competitions = competitionRepository.findCompetitionSortedByCreatedDate(keyword, pageable);
        if (competitions.isEmpty()) {
            throw new EntityNotFoundException("대회가 존재하지 않습니다.");
        }
        return competitionRepository.findCompetitionSortedByCreatedDate(keyword, pageable);
    }

    private static void updateCompetition(Competition competition, CompetitionDto dto) {
        competition.setName(dto.getName());
        competition.setCategory(dto.getSportCategory());
        competition.setRecruitingStart(dto.getRecruitingStart());
        competition.setRecruitingEnd(dto.getRecruitingEnd());
        competition.setStartDate(dto.getStartDate());
        competition.setTotalPrize(dto.getTotalPrize());
        competition.setContent(dto.getContent());
        competition.setLocation(dto.getLocation());
        competition.setLocationDetail(dto.getLocationDetail());
        competition.setMaxPlayer(dto.getMaxPlayer());
        competition.setMaxViewer(dto.getMaxViewer());
        competition.setPosters(dto.getPosters());
        competition.setAgreements(dto.getAgreements());
    }

    /*
    *   대회 생성/수정 시 필수 정보 체크
     */

    private static void checkRequiredInfo(Competition newCompetition) {
        String errorMessage = "";
        if (newCompetition.getName() == null) errorMessage += "\n대회 이름";
        if (newCompetition.getHost() == null) errorMessage += "\n대회 주최자";
        if (newCompetition.getCategory() == null) errorMessage += "\n대회 종목";
        if (newCompetition.getContent() == null) errorMessage += "\n대회 내용";
        if (newCompetition.getLocation() == null) errorMessage += "\n대회 장소";
        if (newCompetition.getLocationDetail() == null) errorMessage += "\n대회 장소 상세";
        if (newCompetition.getStartDate() == null) errorMessage += "\n대회 시작일";
        if (newCompetition.getRecruitingStart() == null) errorMessage += "\n대회 모집 시작일";
        if (newCompetition.getRecruitingEnd() == null) errorMessage += "\n대회 모집 종료일";
        if (newCompetition.getCompetitionType() == null) errorMessage += "\n대회 프리미엄";
        if (errorMessage.length() > 0) {
            errorMessage = "필수 정보가 없습니다." + errorMessage;
            throw new IllegalArgumentException(errorMessage);
        }
    }
    private static void checkTimeValidity(Competition newCompetition) {
        if (newCompetition.getStartDate().isBefore(newCompetition.getRecruitingEnd())) {
            throw new IllegalArgumentException("대회 시작일이 모집 종료일보다 빠릅니다.");
        }
        if (newCompetition.getRecruitingStart().isAfter(newCompetition.getRecruitingEnd())) {
            throw new IllegalArgumentException("모집 종료일이 모집 시작일보다 빠릅니다.");
        }
    }

}
