package PlayMakers.SportsIT.competitions.service;

import PlayMakers.SportsIT.competitions.domain.Category;
import PlayMakers.SportsIT.competitions.domain.Competition;
import PlayMakers.SportsIT.competitions.enums.CompetitionState;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.exceptions.BusinessException;
import PlayMakers.SportsIT.exceptions.ErrorCode;
import PlayMakers.SportsIT.competitions.repository.CompetitionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompetitionServiceImpl_v2 implements CompetitionService {
    private final CompetitionRepository competitionRepository;
    @Override
    @Transactional
    public Competition createCompetition(Member host, Competition competition, Set<Category> categories) {
        log.info("대회 생성 -> member:{}", host.getUid());
        // 권한 확인은 Controller에서 확인
        // competition은 CompetitionDto.Form에서 toEntity()로 변환
        // competition에 host, categories 추가
        competition.addHost(host);
        competition.addCategories(categories);
        competition.setState(CompetitionState.getCompetitionState(competition.getRecruitingStart(), competition.getRecruitingEnd(), competition.getStartDate(), competition.getEndDate()));
        //competition.setHost(host);
        //competition.setCategories(categories);
        return competitionRepository.save(competition);
    }

    @Override
    @Transactional
    public Competition updateCompetition(Long competitionId, Competition updated) {
        log.info("대회 수정 -> competitionId:{}", competitionId);
        // 권한 확인은 Controller에서 확인
        // competitionId로 competition 조회
        Competition find = competitionRepository.findByCompetitionId(competitionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 대회입니다."));
        // competition 수정
        return find.update(updated);
    }

    @Override
    public Competition findCompetition(Long competitionId) {
        log.info("대회 조회 -> competitionId:{}", competitionId);
        return competitionRepository.findByCompetitionId(competitionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 대회입니다."));
    }

    @Override
    public Slice<Competition> findCompetitionsWithConditions(String keyword, List<String> filteringConditions, String orderBy, int page, int size) {
        log.info("대회 검색 -> keyword:{}, filteringConditions:{}, orderBy:{}, page:{}, size:{}", keyword, filteringConditions, orderBy, page, size);

        Pageable pageable = getPageableProperties(orderBy, page, size);

        return competitionRepository.findCompetitionBySlice(keyword, filteringConditions, pageable);
    }

    @Override
    public Slice<Competition> findCompetitionsWithHostId(Long hostId, int page, int size) {
        log.info("대회 검색 -> hostId:{}, page:{}, size:{}", hostId, page, size);

        Pageable pageable = getPageableProperties(null, page, size);

        return competitionRepository.findCompetitionsBySliceWithHostUid(hostId, pageable);
    }

    @Override
    @Transactional
    public void deleteCompetition(Long competitionId) {
        log.info("대회 삭제 -> competitionId:{}", competitionId);
        // 권한 확인은 Controller에서 확인
        // competitionId로 competition 조회
        Competition find = competitionRepository.findByCompetitionId(competitionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 대회입니다."));
        // competition 삭제
        competitionRepository.delete(find);
    }

    private static Pageable getPageableProperties(String orderBy, int page, int size) {
        Pageable pageable;
        if(orderBy != null && !orderBy.isEmpty()) pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, orderBy));
        else pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        return pageable;
    }
}
