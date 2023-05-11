package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.repository.ImageRepository;
import PlayMakers.SportsIT.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {
    @Autowired
    private final S3Uploader s3Uploader;

    @Autowired
    private final ImageRepository imageRepository;

    @PostMapping
    public String upload(@RequestBody MultipartFile image) throws IOException{
        String url = s3Uploader.upload(image, "test");

        return url;
    }
}
