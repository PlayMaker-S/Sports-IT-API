package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.JoinCompetitionTemplate;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JoinCompetitionTemplateService {
    public static final String COL_NAME = "joinCompetitionTemplate";
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference("server/sportsit-test/transactions");

    public String saveTemplate(JoinCompetitionTemplate template){
        DatabaseReference templateRef = ref.child(COL_NAME);
        DatabaseReference newTemplateRef = templateRef.push();
        newTemplateRef.setValueAsync(template);

        return newTemplateRef.getKey();
    }

}
