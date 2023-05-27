package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.CompetitionTemplate;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

@Slf4j
@Repository
public class CompetitionTemplateRepository {
    public static final String COL_NAME = "template";

    public String saveTemplate(CompetitionTemplate template) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> newTemplateRef = db.collection(COL_NAME).add(template); // 새 document id 자동 생성
        log.info("새 template 생성: {}", newTemplateRef);

        return newTemplateRef.get().getId();
    }

    public void updateTemplate(String templateId, CompetitionTemplate template) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        db.collection(COL_NAME).document(templateId).set(template);
        log.info("template 수정: {}", templateId);
    }
    public CompetitionTemplate findTemplate(String templateId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference templateRef = db.collection(COL_NAME).document(templateId);
        ApiFuture<DocumentSnapshot> future = templateRef.get();
        CompetitionTemplate template = future.get().toObject(CompetitionTemplate.class);
        return template;
    }
    public void deleteTemplate(String templateId) {
        Firestore db = FirestoreClient.getFirestore();
        db.collection(COL_NAME).document(templateId).delete();
        log.info("template 삭제: {}", templateId);
    }
}
