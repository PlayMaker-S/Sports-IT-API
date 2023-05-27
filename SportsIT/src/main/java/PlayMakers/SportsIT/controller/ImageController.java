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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/image")
public class ImageController {
    private final S3Uploader s3Uploader;
    private final PosterService posterService;
    private final AgreementService agreementService;
    private final CompetitionService competitionService;

    @PostMapping
    public String upload(@RequestBody MultipartFile image) throws IOException{
        String url = s3Uploader.upload(image, "test");

        return url;
    }

    @PostMapping("/poster")
    public ResponseEntity<List<Poster>> uploadPoster(@RequestBody List<MultipartFile> posters, Long competitionId) throws IOException{
        Competition competition = competitionService.findById(competitionId);

        List<String> savedUrls = s3Uploader.uploadImages(posters, "poster/"+competitionId);

        List<Poster> savedPosters = posterService.savePosters(savedUrls, competition);

        return ResponseEntity.created(URI.create("/" + savedPosters.get(0).getPosterUrl()))
                .body(savedPosters); // 201
    }
    // @GetMapping("/poster/{competitionId}")

    @PostMapping("/agreements")
    public ResponseEntity<List<Agreement>> uploadAgreement (@RequestBody List<MultipartFile> agreements, Long competitionId) throws IOException {
        Competition competition = competitionService.findById(competitionId);

        List<String> savedUrls = s3Uploader.uploadImages(agreements, "agreement/"+competitionId);

        List<Agreement> savedAgreements = agreementService.saveAgreements(savedUrls, competition);

        return ResponseEntity.created(URI.create("/" + savedAgreements.get(0).getAgreementUrl()))
                .body(savedAgreements); // 201
    }

    // 핸들러
    @ExceptionHandler(IOException.class)
    public ResponseEntity handleIOException(IOException e) {
        return ResponseEntity.badRequest().build();
    }
}
