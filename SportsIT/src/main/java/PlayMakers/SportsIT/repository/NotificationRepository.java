package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.Notification;
import PlayMakers.SportsIT.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{

    Page<Notification> findByMemberAndNotificationType(Member member, NotificationType type, Pageable pageable);
}
