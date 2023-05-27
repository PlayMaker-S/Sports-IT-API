package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.JoinCompetitionTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompetitionTemplateService {
    public static final String COL_NAME = "joinCompetitionTemplate";
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference("server/sportsit-test/transactions");


    public String saveTemplate(JoinCompetitionTemplate template){
        DatabaseReference templateRef = ref.child(COL_NAME);
        DatabaseReference newTemplateRef = templateRef.push();
        newTemplateRef.setValueAsync(template);

        return newTemplateRef.getKey();
    }
    public void updateTemplate(String templateId, JoinCompetitionTemplate template) {
        DatabaseReference templateRef = ref.child(COL_NAME).child(templateId);
        templateRef.setValueAsync(template);
    }

    public JoinCompetitionTemplate getTemplate(String templateId) {
        DatabaseReference templateRef = ref.child(COL_NAME).child(templateId);
        // 데이터 검색
        JoinCompetitionTemplate template = new JoinCompetitionTemplate();
        List<JoinCompetitionTemplate.Sector> sectors = new ArrayList<JoinCompetitionTemplate.Sector>();
        List<JoinCompetitionTemplate.SubSector> subSectors1 = new ArrayList<JoinCompetitionTemplate.SubSector>();
        List<JoinCompetitionTemplate.SubSector> subSectors2 = new ArrayList<JoinCompetitionTemplate.SubSector>();
        List<JoinCompetitionTemplate.SubSector> subSectors3 = new ArrayList<JoinCompetitionTemplate.SubSector>();
        JoinCompetitionTemplate.SubSector subSector1 = new JoinCompetitionTemplate.SubSector("-65kg");
        JoinCompetitionTemplate.SubSector subSector2 = new JoinCompetitionTemplate.SubSector("-75kg");
        JoinCompetitionTemplate.SubSector subSector3 = new JoinCompetitionTemplate.SubSector("+75kg");
        subSectors1.addAll(List.of(subSector1, subSector2, subSector3));
        subSectors2.addAll(List.of(subSector1, subSector2, subSector3));
        subSectors3.addAll(List.of(subSector1, subSector2, subSector3));
        JoinCompetitionTemplate.Sector sector1 = new JoinCompetitionTemplate.Sector("프로", 70000L, 20000L, true, subSectors1);
        JoinCompetitionTemplate.Sector sector2 = new JoinCompetitionTemplate.Sector("세미프로", 50000L, 10000L, true, subSectors2);
        JoinCompetitionTemplate.Sector sector3 = new JoinCompetitionTemplate.Sector("아마차워", 30000L, 5000L, false, subSectors3);
        sectors.addAll(List.of(sector1, sector2, sector3));
        template.setSectors(sectors);

        return template;
    }

}
