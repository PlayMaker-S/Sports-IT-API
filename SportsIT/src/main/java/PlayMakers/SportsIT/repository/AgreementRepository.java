package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Agreement;
import PlayMakers.SportsIT.competitions.domain.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgreementRepository extends JpaRepository<Agreement, Long> {
    Optional<Agreement> findByAgreementUrl(String url);
    List<Agreement> findAllByCompetition(Competition competition);
    Void deleteByAgreementUrl(String url);
}
