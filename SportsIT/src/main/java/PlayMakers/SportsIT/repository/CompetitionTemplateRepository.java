package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.JoinCompetitionTemplate;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

@Repository
@Slf4j
public class CompetitionTemplateRepository {
    public static final String COL_NAME = "template";

    public String saveTemplate(JoinCompetitionTemplate template) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> newTemplateRef = db.collection(COL_NAME).add(template);
        log.info("새 template 생성: {}", newTemplateRef);

        return newTemplateRef.get().getId();
    }
}
