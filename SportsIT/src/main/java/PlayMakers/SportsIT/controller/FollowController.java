package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.service.FollowService;
import PlayMakers.SportsIT.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Setter
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {
    private final FollowService followService;
    private final MemberService memberService;

    @PostMapping("/{followingId}")
    public ResponseEntity<Object> follow(@PathVariable Long followingId,
                                         @AuthenticationPrincipal User user)  throws Exception {
        followService.follow(memberService.findOne(user.getUsername()).getUid(), followingId);
        Map<String, Object> res = Map.of("result", "success");

        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{followingId}")
    public ResponseEntity<Object> unfollow(@PathVariable Long followingId,
                                           @AuthenticationPrincipal User user) throws Exception {
        followService.unfollow(memberService.findOne(user.getUsername()).getUid(), followingId);

        Map<String, Object> res = Map.of("result", "success");
        return ResponseEntity.ok(res);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        Map<String, Object> res = Map.of("result", "fail", "message", e.getMessage());
        return ResponseEntity.badRequest().body(res);
    }
}

