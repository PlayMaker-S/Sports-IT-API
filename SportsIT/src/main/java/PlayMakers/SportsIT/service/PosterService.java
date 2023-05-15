package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.Poster;
import PlayMakers.SportsIT.repository.PosterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PosterService {
    private final PosterRepository posterRepository;

    public List<Poster> savePosters(List<String> posterUrls, Competition competition) {
        List<Poster> savedPosters = new ArrayList<>();

        for (String url : posterUrls) {
            log.info("url : {}", url);
            Poster poster = new Poster(url, competition);
            log.info("poster : {}", poster);
            savedPosters.add(posterRepository.save(poster));
        }
        //return posterRepository.saveAll(savedPosters);
        log.info("posters: {}", savedPosters);
        return savedPosters;
    }

}
