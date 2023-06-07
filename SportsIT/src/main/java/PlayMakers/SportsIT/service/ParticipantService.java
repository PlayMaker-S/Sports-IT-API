package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.CompetitionForm;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.Participant;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import PlayMakers.SportsIT.repository.ParticipantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ParticipantService {
    private final MemberRepository memberRepository;
    private final CompetitionRepository competitionRepository;
    private final ParticipantRepository participantRepository;

    public List<Participant> findAllByCompetitionId(Long competitionId){
        return participantRepository.findAllByCompetitionCompetitionId(competitionId);
    }

    public List<Participant> findAllByCompetitionIdAndSectorTitle(Long competitionId, String sectorTitle){
        return participantRepository.findAllByCompetitionCompetitionIdAndIdSectorTitle(competitionId, sectorTitle);
    }

    public List<Participant> findAllByCompetitionIdAndSectorTitleAndSubSectorName(Long competitionId, String sectorTitle, String subSectorName){
        return participantRepository.findAllByCompetitionCompetitionIdAndIdSectorTitleAndIdSubSectorName(competitionId, sectorTitle, subSectorName);
    }

    public Participant save(Member member, Competition competition, String sectorTitle, String subSectorName){
        Participant participant = Participant.builder()
                .member(member)
                .competition(competition)
                .id(Participant.ParticipantId.builder()
                        .uid(member.getUid())
                        .competitionId(competition.getCompetitionId())
                        .sectorTitle(sectorTitle)
                        .subSectorName(subSectorName)
                        .build()).build();
        return participantRepository.save(participant);
    }

    public List<Participant> saveAll(List<Participant> participants){
        return participantRepository.saveAll(participants);
    }

    public List<Participant> parseAndSaveParticipants( Member member, Competition competition, CompetitionForm form) {
        List<CompetitionForm.Sector> sectors = form.getSectors();
        List<Participant> newParticipants = new ArrayList<>();
        for(CompetitionForm.Sector sector : sectors){
            List<CompetitionForm.SubSector> subSectors = sector.getSubSectors();
            for(CompetitionForm.SubSector subSector : subSectors){
                if(subSector.isChecked()){
                    newParticipants.add(save(member, competition, sector.getTitle(), subSector.getName()));
                }
            }
        }
        return newParticipants;
    }
}
