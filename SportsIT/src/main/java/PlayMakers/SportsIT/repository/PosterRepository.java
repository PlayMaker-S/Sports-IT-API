package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Poster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PosterRepository extends JpaRepository<Poster, Long> {
    Optional<Poster> findByPosterUrl(String url);
    List<Poster> findByCompetitionCompetitionId(Long id);
}
