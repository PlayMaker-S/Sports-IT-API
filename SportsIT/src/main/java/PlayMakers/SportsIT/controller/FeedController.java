package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.BodyInfo;
import PlayMakers.SportsIT.domain.Feed;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.service.BodyInfoService;
import PlayMakers.SportsIT.service.FeedService;
import PlayMakers.SportsIT.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feed")
public class FeedController {
    private final FeedService feedService;
    private final MemberService memberService;
}
