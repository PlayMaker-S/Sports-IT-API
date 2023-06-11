package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.Notification;
import PlayMakers.SportsIT.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{

    Page<Notification> findByReceiverAndNotificationType(Member member, NotificationType type, Pageable pageable);
    int countByReceiverAndChecked(Member member, boolean checked);
}
