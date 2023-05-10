package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {
    @Autowired
    private final S3Uploader s3Uploader;

    @PostMapping
    public String upload(@RequestParam(value="image") MultipartFile image) throws IOException{
        String url = s3Uploader.upload(image, "test");
        return url;
    }
}
