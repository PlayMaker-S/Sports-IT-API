package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.JoinCompetition;
import PlayMakers.SportsIT.dto.JoinCompetitionDto;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.JoinCompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JoinCompetitionService {
    final private CompetitionRepository competitionRepository;
    final private MemberRepository memberRepository;
    final private JoinCompetitionRepository joinCompetitionRepository;

    public void joinCompetition(JoinCompetitionDto dto) {
        log.info("대회 참가 요청: {}", dto);

        JoinCompetition join = dto.toEntity();
        joinCompetitionRepository.save(join);
    }

    public void updateJoinCompetition(JoinCompetitionDto dto){
        log.info("대회 참가 정보 수정 요청: {}", dto);

        JoinCompetition target = joinCompetitionRepository.findByIdUidAndIdCompetitionId(dto.getUid(), dto.getCompetitionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 대회에 참가한 회원이 존재하지 않습니다."));

        //target.update(dto);
    }
}
