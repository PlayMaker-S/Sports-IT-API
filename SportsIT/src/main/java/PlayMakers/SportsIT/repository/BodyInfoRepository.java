package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.BodyInfo;
import PlayMakers.SportsIT.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BodyInfoRepository extends JpaRepository<BodyInfo, Long> {
    Optional<BodyInfo> findByMemberUid(Long memberId);
}
