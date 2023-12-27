package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.Agreement;
import PlayMakers.SportsIT.competitions.domain.Competition;
import PlayMakers.SportsIT.dto.AgreementDto;
import PlayMakers.SportsIT.repository.AgreementRepository;
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

    public List<Agreement> saveAgreements(List<AgreementDto> agreements, Competition competition) {
        List<Agreement> savedAgreements = new ArrayList<>();
        for(AgreementDto agreement : agreements) {
            log.info("agreement: {}", agreement.getAgreementUrl());
            Agreement newAgreement = Agreement.builder()
                                    .agreementUrl(agreement.getAgreementUrl())
                                    .name(agreement.getAgreementName())
                                    .competition(competition)
                                    .build();

            agreementRepository.save(newAgreement);
        }

        log.info("savedAgreements: {}", savedAgreements);
        return savedAgreements;
    }
    public List<Agreement> findAgreementsByCompetition(Competition competition) {
        return agreementRepository.findAllByCompetition(competition);
    }

    public void deleteAgreement(String agreementUrl) {
        agreementRepository.deleteByAgreementUrl(agreementUrl);
    }

}
