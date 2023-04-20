package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.SportCategory;
import PlayMakers.SportsIT.dto.CompetitionDto;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class CompetitionService {
    private final CompetitionRepository competitionRepository;
    private final MemberRepository memberRepository;


    public Competition createCompetition(CompetitionDto dto) {
        Member host = memberRepository.findById(dto.getHostId()).orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        Competition newCompetition = Competition.builder()
                .name(dto.getName())
                .host(host)
                .category(SportCategory.valueOf(dto.getSportCategory()))
                .content(dto.getContent())
                .location(dto.getLocation())
                .state(dto.getState())
                .stateDetail(dto.getStateDetail())
                .startDate(dto.getStartDate())
                .recruitingStart(dto.getRecruitingStart())
                .recruitingEnd(dto.getRecruitingEnd())
                .build();

        return competitionRepository.save(newCompetition);
    }

}
