package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.competitions.domain.Competition;
import PlayMakers.SportsIT.domain.Poster;
import PlayMakers.SportsIT.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
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
    private final MemberService memberService;

    @PostMapping
    public String upload(@RequestBody MultipartFile image) throws IOException{
        String url = s3Uploader.upload(image, "test");

        return url;
    }

    @PostMapping("/poster/{competitionId}")
    public ResponseEntity<Object> uploadPoster(@RequestBody List<MultipartFile> posters,
                                                     @PathVariable Long competitionId) throws IOException{
        Competition competition = competitionService.findById(competitionId);

        List<String> savedUrls = s3Uploader.uploadImages(posters, "poster/"+competitionId);

        List<Poster> savedPosters = posterService.savePosters(savedUrls, competition);

        Object res = new HashMap<String, Object>() {{
            put("success", true);
        }};

        return ResponseEntity.created(URI.create("/" + savedPosters.get(0).getPosterUrl()))
                .body(res); // 201
    }
    // @GetMapping("/poster/{competitionId}")

    @PostMapping("/agreements/{competitionId}")
    public ResponseEntity<List<String>> uploadAgreements (@RequestBody List<MultipartFile> agreements,
                                                          @PathVariable Long competitionId) throws IOException {
        List<String> savedUrls = s3Uploader.uploadImages(agreements, "agreement/"+competitionId);

        return ResponseEntity.created(URI.create("/" + savedUrls.get(0)))
                .body(savedUrls); // 201
    }

    @PostMapping("member/{memberId}")
    public ResponseEntity<String> uploadProfileImage(@RequestBody MultipartFile ProfileImage, @PathVariable Long memberId) throws IOException {
        String savedUrl = s3Uploader.upload(ProfileImage, "Profile/"+memberId);

        return ResponseEntity.created(URI.create("/" + savedUrl)).body(savedUrl);
    }

    // 핸들러
    @ExceptionHandler(IOException.class)
    public ResponseEntity handleIOException(IOException e) {
        return ResponseEntity.badRequest().build();
    }
}
