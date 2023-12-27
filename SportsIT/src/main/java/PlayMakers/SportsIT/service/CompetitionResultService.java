package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.competitions.domain.Competition;
import PlayMakers.SportsIT.domain.CompetitionResult;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.dto.CompetitionResultDto;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.CompetitionResultRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CompetitionResultService {

    private final CompetitionResultRepository competitionResultRepository;
    private final CompetitionRepository competitionRepository;
    private final MemberRepository memberRepository;

    public String createCompetitionResult(List<CompetitionResultDto> dtos) {
        for(CompetitionResultDto dto : dtos){
            Competition competition = competitionRepository.findByCompetitionId(dto.getCompetitionId());
            Optional<Member> member = memberRepository.findById(dto.getUid());
            if(member.isEmpty()){
                return "No Member with that UID";
            }
            CompetitionResult competitionResult = CompetitionResult.builder()
                    .competition(competition)
                    .historyDate(competition.getEndDate())
                    .content(dto.getContent())
                    .member(member.get())
                    .build();
            competitionResultRepository.save(competitionResult);
        }
        return "Success";
    }
}
