package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.Agreement;
import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.Poster;
import PlayMakers.SportsIT.repository.AgreementRepository;
import PlayMakers.SportsIT.repository.PosterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AgreementService {
    private final AgreementRepository agreementRepository;

    public List<Agreement> saveAgreements(List<String> agreementUrls, Competition competition) {
        List<Agreement> savedAgreements = new ArrayList<>();

        for (String url : agreementUrls) {
            log.info("url : {}", url);
            Agreement poster = new Agreement(url, competition);
            log.info("poster : {}", poster);
            savedAgreements.add(agreementRepository.save(poster));
        }

        log.info("posters: {}", savedAgreements);
        return savedAgreements;
    }

}
