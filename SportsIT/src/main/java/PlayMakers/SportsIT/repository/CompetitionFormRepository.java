package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.CompetitionForm;
import PlayMakers.SportsIT.domain.CompetitionTemplate;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Repository
public class CompetitionFormRepository {
    public static final String COL_NAME = "form";

    public String saveForm(CompetitionForm form) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> newTemplateRef = db.collection(COL_NAME).add(form); // 새 document id 자동 생성
        log.info("새 form 생성: {}", newTemplateRef);

        return newTemplateRef.get().getId();
    }

    public void updateForm(String formId, CompetitionForm form) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        db.collection(COL_NAME).document(formId).set(form);
        log.info("form 수정: {}", form);
    }
    public CompetitionForm findForm(String formId) throws ExecutionException, InterruptedException {
        log.info("form 조회: {}", formId);
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference formRef = db.collection(COL_NAME).document(formId);
        ApiFuture<DocumentSnapshot> future = formRef.get();
        return future.get().toObject(CompetitionForm.class);
    }
    public void deleteForm(String formId) {
        Firestore db = FirestoreClient.getFirestore();
        db.collection(COL_NAME).document(formId).delete();
        log.info("form 삭제: {}", formId);
    }

}
