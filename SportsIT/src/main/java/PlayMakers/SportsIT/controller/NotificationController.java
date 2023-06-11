package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.service.MemberService;
import PlayMakers.SportsIT.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;
    private final MemberService memberService;

    @GetMapping("/count-new")
    ResponseEntity<Object> countNewNotification(@AuthenticationPrincipal User user) throws Exception {
        Long count = (long) notificationService.getUncheckedNotificationCount(memberService.findOne(user.getUsername()));
        Map<String, Object> res = new HashMap<>() {{
            put("success", true);
            put("count", count);
        }};
        return ResponseEntity.ok(res);
    }
    @GetMapping("/list/activity")
    ResponseEntity<Object> getActivityNotificationList(@AuthenticationPrincipal User user) throws Exception {
        Map<String, Object> res = new HashMap<>() {{
            put("success", true);
            put("result", notificationService.getActivityNotificationsByMember(memberService.findOne(user.getUsername())));
        }};
        return ResponseEntity.ok(res);
    }

    @GetMapping("/list/competition")
    ResponseEntity<Object> getCompetitionNotificationList(@AuthenticationPrincipal User user) throws Exception {
        Map<String, Object> res = new HashMap<>() {{
            put("success", true);
            put("result", notificationService.getCompetitionNotificationsByMember(memberService.findOne(user.getUsername())));
        }};
        return ResponseEntity.ok(res);
    }
    @GetMapping("/check/{notificationId}")
    ResponseEntity<Object> checkNotification(@AuthenticationPrincipal User user,
                                             @PathVariable Long notificationId) throws Exception {
        notificationService.checkNotification(notificationId, memberService.findOne(user.getUsername()));
        Map<String, Object> res = new HashMap<>() {{
            put("success", true);
        }};
        return ResponseEntity.ok(res);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        Map<String, Object> res = new HashMap<>() {{
            put("success", false);
            put("message", e.getMessage());
        }};
        return ResponseEntity.badRequest().body(res);
    }
}
