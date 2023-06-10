package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.CompetitionResult;
import PlayMakers.SportsIT.domain.Feed;

import org.hibernate.mapping.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionResultRepository extends JpaRepository<CompetitionResult, Long> {
}
