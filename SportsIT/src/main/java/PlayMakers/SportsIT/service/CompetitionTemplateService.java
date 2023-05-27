package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.CompetitionTemplate;
import PlayMakers.SportsIT.repository.CompetitionTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompetitionTemplateService {
    private final CompetitionTemplateRepository competitionTemplateRepository;

    public String saveTemplate(CompetitionTemplate template) throws ExecutionException, InterruptedException {
        log.info("template 생성 요청");
        return competitionTemplateRepository.saveTemplate(template);
    }
    public void updateTemplate(String templateId, CompetitionTemplate template) throws ExecutionException, InterruptedException {
        competitionTemplateRepository.updateTemplate(templateId, template);
    }
    public CompetitionTemplate getTemplate(String templateId) throws ExecutionException, InterruptedException {
        return competitionTemplateRepository.findTemplate(templateId);
    }
    public void deleteTemplate(String templateId) {
        competitionTemplateRepository.deleteTemplate(templateId);
    }

//    public JoinCompetitionTemplate getTemplate2(String templateId) {
//        DatabaseReference templateRef = ref.child(COL_NAME).child(templateId);
//        // 데이터 검색
//        JoinCompetitionTemplate template = new JoinCompetitionTemplate();
//        List<JoinCompetitionTemplate.Sector> sectors = new ArrayList<JoinCompetitionTemplate.Sector>();
//        List<JoinCompetitionTemplate.SubSector> subSectors1 = new ArrayList<JoinCompetitionTemplate.SubSector>();
//        List<JoinCompetitionTemplate.SubSector> subSectors2 = new ArrayList<JoinCompetitionTemplate.SubSector>();
//        List<JoinCompetitionTemplate.SubSector> subSectors3 = new ArrayList<JoinCompetitionTemplate.SubSector>();
//        JoinCompetitionTemplate.SubSector subSector1 = new JoinCompetitionTemplate.SubSector("-65kg");
//        JoinCompetitionTemplate.SubSector subSector2 = new JoinCompetitionTemplate.SubSector("-75kg");
//        JoinCompetitionTemplate.SubSector subSector3 = new JoinCompetitionTemplate.SubSector("+75kg");
//        subSectors1.addAll(List.of(subSector1, subSector2, subSector3));
//        subSectors2.addAll(List.of(subSector1, subSector2, subSector3));
//        subSectors3.addAll(List.of(subSector1, subSector2, subSector3));
//        JoinCompetitionTemplate.Sector sector1 = new JoinCompetitionTemplate.Sector("프로", 70000L, 20000L, true, subSectors1);
//        JoinCompetitionTemplate.Sector sector2 = new JoinCompetitionTemplate.Sector("세미프로", 50000L, 10000L, true, subSectors2);
//        JoinCompetitionTemplate.Sector sector3 = new JoinCompetitionTemplate.Sector("아마차워", 30000L, 5000L, false, subSectors3);
//        sectors.addAll(List.of(sector1, sector2, sector3));
//        template.setSectors(sectors);
//
//        return template;
//    }

}
