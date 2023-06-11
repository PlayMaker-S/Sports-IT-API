package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Long countByFollowerUid(Long followerId);
    Long countByFollowingUid(Long followingId);
    List<Follow> findByFollowerUid(Long followerId); // Follower가 팔로우하는 사람들
    List<Follow> findByFollowingUid(Long followingId); // Following을 팔로우하는 사람들
}
