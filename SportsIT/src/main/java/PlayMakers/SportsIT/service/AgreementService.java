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
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AgreementService {
    private final AgreementRepository agreementRepository;

    public List<Agreement> saveAgreements(Map<String,String> agreementMap, Competition competition) {
        List<Agreement> savedAgreements = new ArrayList<>();

        for (String name : agreementMap.keySet()) {
            log.info("name : {}", name);
            Agreement agreement = new Agreement(agreementMap.get(name), name, competition);
            savedAgreements.add(agreement);
        }
        agreementRepository.saveAll(savedAgreements);

        log.info("savedAgreements: {}", savedAgreements);
        return savedAgreements;
    }

}
