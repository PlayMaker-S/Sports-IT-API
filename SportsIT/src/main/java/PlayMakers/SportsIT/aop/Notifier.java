package PlayMakers.SportsIT.aop;

import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.CompetitionResult;
import PlayMakers.SportsIT.domain.JoinCompetition;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.enums.NotificationTitle;
import PlayMakers.SportsIT.service.JoinCompetitionService;
import PlayMakers.SportsIT.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class Notifier {
    private final NotificationService notificationService;
    private final JoinCompetitionService joinCompetitionService;

    @AfterReturning(value = "execution(* PlayMakers.SportsIT.service.MemberService.join(..))", returning = "member")
    public void sendJoinNotification(Member member) {
        log.info("회원가입 축하 알림 발송");
        notificationService.sendNotification(member, NotificationTitle.JOIN, "/member/profile", null, null);
    }
    @Async
    @AfterReturning(value = "execution(* PlayMakers.SportsIT.scheduler.CompetitionScheduler.updateCompetitionState(..))", returning = "competition")
    public void sendCompetitionStateNotification(Competition competition) {
        if (competition == null) return;
        log.info("경기 상태 변경 알림 발송");
        Member sender = competition.getHost();
        String link = "/competitions/"+competition.getCompetitionId();
        String competitionName = competition.getName();
        NotificationTitle title;
        switch (competition.getState()) {
            case RECRUITING_END -> {
                title = NotificationTitle.RECRUITING_END;
            }
            case IN_PROGRESS -> {
                title = NotificationTitle.STARTED;
            }
            case END -> {
                title = NotificationTitle.END;
                notificationService.sendNotification(sender, title, link, sender, competitionName);
            }
            default -> {
                log.info("경기 상태 변경 알림 발송 실패");
                return;
            }
        }
        for(Member receiver : joinCompetitionService.getJoinedMembersByCompetition(competition)){
            notificationService.sendNotification(receiver, title, link, sender, competitionName);
        }

    }

    @AfterReturning(value = "execution(* PlayMakers.SportsIT.service.JoinCompetitionService.join(..))", returning = "joinCompetition")
    public void sendJoinCompetitionNotification(JoinCompetition joinCompetition) {
        log.info("경기 참가 알림 발송");
        Member sender = joinCompetition.getMember();
        Member receiver = joinCompetition.getCompetition().getHost();
        String link = "/competitions/"+joinCompetition.getCompetition().getCompetitionId();
        if(joinCompetition.getJoinType().equals(JoinCompetition.joinType.PLAYER))
            notificationService.sendNotification(receiver, NotificationTitle.NEW_PLAYER, link, sender, joinCompetition.getCompetition().getName());
        else if(joinCompetition.getJoinType().equals(JoinCompetition.joinType.VIEWER))
            notificationService.sendNotification(receiver, NotificationTitle.NEW_SPECTATOR, link, sender, joinCompetition.getCompetition().getName());
    }

    @AfterReturning(value = "execution(* PlayMakers.SportsIT.repository.CompetitionResultRepository.save(..))", returning = "result")
    public void sendCompetitionResultNotification(CompetitionResult result) {
        log.info("경기 결과 알림 발송");
        Competition competition = result.getCompetition();
        Member sender = competition.getHost();
        String link = "competitions/result"+competition.getCompetitionId();
        String competitionName = competition.getName();
        NotificationTitle title = NotificationTitle.CHECK_RESULT;
        for(Member receiver : joinCompetitionService.getJoinedMembersByCompetition(competition)){
            notificationService.sendNotification(receiver, title, link, sender, competitionName);
        }
    }

}
