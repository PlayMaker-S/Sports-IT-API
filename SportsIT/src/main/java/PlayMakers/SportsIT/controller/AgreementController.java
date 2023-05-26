package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.Agreement;
import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.Poster;
import PlayMakers.SportsIT.service.AgreementService;
import PlayMakers.SportsIT.service.CompetitionService;
import PlayMakers.SportsIT.service.PosterService;
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
    private final CompetitionService competitionService;

    @PostMapping("/upload") // 대회 규정 S3 저장 후 저장된 URL 리스트 반환
    public ResponseEntity<List<String>> uploadAgreements (@RequestBody List<MultipartFile> agreements, Long competitionId) throws IOException {
        Competition competition = competitionService.findById(competitionId);

        List<String> savedUrls = s3Uploader.uploadImages(agreements, "agreement/"+competitionId);

        return ResponseEntity.created(URI.create("/" + savedUrls.get(0)))
                .body(savedUrls); // 201
    }
    @PostMapping("/save/{competitionId}")
    public ResponseEntity<?> saveAgreements (@RequestParam Long competitionId,
                                             @RequestBody List<String> agreementUrls,
                                             @RequestBody List<String> agreementNames) {
        Map<String, Object> res = new HashMap<>();

        Map<String, String> agreementMap = new HashMap<>();

        if (agreementUrls.size() != agreementNames.size()) {
            String errMsg = "규정 명칭과 규정 파일의 개수가 일치하지 않습니다.";
            throw new IllegalArgumentException(errMsg); //
        }
        for (int i = 0; i < agreementUrls.size(); i++) {
            agreementMap.put(agreementNames.get(i), agreementUrls.get(i));
        }

        Competition competition = competitionService.findById(competitionId);

        List<Agreement> saved = agreementService.saveAgreements(agreementMap, competition);

        res.put("success", true);
        res.put("result", saved);

        return ResponseEntity.created(URI.create("/" + saved.get(0).getAgreementUrl()))
                .body(res); // 201
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
