package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {

}
