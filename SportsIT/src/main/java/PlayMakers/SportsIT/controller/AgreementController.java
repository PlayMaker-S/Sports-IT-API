package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.Agreement;
import PlayMakers.SportsIT.competitions.domain.Competition;
import PlayMakers.SportsIT.dto.AgreementDto;
import PlayMakers.SportsIT.service.AgreementService;
import PlayMakers.SportsIT.competitions.service.CompetitionServiceImpl_v1;
import PlayMakers.SportsIT.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/agreement")
public class AgreementController {
    private final S3Uploader s3Uploader;
    private final AgreementService agreementService;
    private final CompetitionServiceImpl_v1 competitionServiceImplv1;

    @PostMapping("/upload/{competitionId}") // 대회 규정 S3 저장 후 저장된 URL 리스트 반환
    public ResponseEntity<Object> uploadAgreements (@RequestBody List<MultipartFile> agreements,
                                                    @PathVariable Long competitionId) throws IOException {
        Competition competition = competitionServiceImplv1.findById(competitionId);

        List<String> savedUrls = s3Uploader.uploadImages(agreements, "agreement/"+competitionId);

        Object res = new HashMap<String, Object>() {{
            put("success", true);
            put("result", savedUrls);
        }};

        return ResponseEntity.created(URI.create("/" + savedUrls.get(0)))
                .body(res); // 201
    }
    @PostMapping("/save/{competitionId}")
    public ResponseEntity<Object> saveAgreements (@PathVariable Long competitionId,
                                                  @RequestBody List<AgreementDto> agreements) {


        Competition competition = competitionServiceImplv1.findById(competitionId);

        List<Agreement> saved = agreementService.saveAgreements(agreements, competition);

        Object res = new HashMap<String, Object>() {{
            put("success", true);
        }};

        return ResponseEntity.created(URI.create("/api/competitions/" + competitionId))
                .body(res); // 201
    }
    @GetMapping()
    public ResponseEntity<Object> getAllAgreements(@RequestParam Long competitionId) {
        Competition target = competitionServiceImplv1.findById(competitionId);
        List<Agreement> agreements = agreementService.findAgreementsByCompetition(target);

        Object res = new HashMap<String, Object>() {{
            put("success", true);
            put("result", agreements);
        }};

        return ResponseEntity.ok(res);
    }

    // 핸들러
    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException e) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", false);
        res.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", false);
        res.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(res);
    }
}
