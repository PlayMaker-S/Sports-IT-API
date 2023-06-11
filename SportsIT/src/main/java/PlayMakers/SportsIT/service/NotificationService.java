package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.Notification;
import PlayMakers.SportsIT.enums.NotificationTitle;
import PlayMakers.SportsIT.enums.NotificationType;
import PlayMakers.SportsIT.repository.MemberRepository;
import PlayMakers.SportsIT.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    public List<Notification> getActivityNotificationsByMember(Member member) {
        log.debug("활동 알림 목록 조회");
        Pageable pageable = PageRequest.of(0, 15);
        return notificationRepository.findByReceiverAndNotificationType(member, NotificationType.ACTIVITY, pageable).getContent();
    }

    public List<Notification> getCompetitionNotificationsByMember(Member member) {
        log.debug("공지 알림 목록 조회");
        Pageable pageable = PageRequest.of(0, 15);
        return notificationRepository.findByReceiverAndNotificationType(member, NotificationType.COMPETITION, pageable).getContent();
    }

    public void checkNotification(Long id, Member viewer) {
        log.debug("알림 확인");
        Notification notification = notificationRepository.findById(id).orElseThrow();
        if (notification.getReceiver().getUid().equals(viewer.getUid())) notification.setChecked(true);
    }
    public int getUncheckedNotificationCount(Member member) {
        log.debug("확인하지 않은 알림 개수 조회");
        return notificationRepository.countByReceiverAndChecked(member, false);
    }

    public void sendNotification(Member receiver, NotificationTitle title, String link, Member sender, String content) {
        log.info("공지 알림 발송");
        String message="";
        NotificationType type = null;
        switch (title) {
            case JOIN -> message = receiver.getName() + "님의 회원가입을 진심으로 환영합니다! FEEL-IT을 채워 내 포르필을 관리하세요!";
            case RECRUITING_END -> message = "대회 모집이 종료되었습니다. 참가자를 확인해보세요!" + " : " + content;
            case START_SOON -> message = "대회가 곧 시작됩니다." + " : " + content;
            case STARTED -> message = "대회가 시작되었습니다." + " : " + content;
            case END -> message = "대회가 종료되었습니다." + " : " + content;
            case NEED_TO_COMPLETE -> message = "대회가 종료되었습니다. 대회 결과를 입력해주세요." + " : " + content;
            case CHECK_RESULT -> message = "대회가 종료되었습니다. 대회 결과를 확인하세요!" + " : " + content;
            case CANCELED -> message = "대회가 취소되었습니다." + " : " + content;
            case NEW_PLAYER -> message = "대회에 새로운 참가자가 등록되었습니다." + " : " + content;
            case NEW_SPECTATOR -> message = "대회에 새로운 관람객이 등록되었습니다." + " : " + content;
            default -> throw new IllegalStateException("Unexpected notification title: " + title);
        }
        Notification notification = Notification.builder()
                .receiver(receiver)
                .title(title)
                .message(message)
                .link(link)
                .notificationType(getNotificationType(title))
                .sender(sender)
                .build();
        notificationRepository.save(notification);
    }

    private NotificationType getNotificationType(NotificationTitle title) {
        NotificationType type = null;
        switch (title) {
            case JOIN -> type = NotificationType.ACTIVITY;
            case RECRUITING_END, START_SOON, STARTED, END, NEED_TO_COMPLETE, CHECK_RESULT, CANCELED -> type = NotificationType.COMPETITION;
        }
        return type;
    }
}
