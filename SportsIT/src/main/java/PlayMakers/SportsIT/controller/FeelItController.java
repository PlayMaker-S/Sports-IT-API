package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.CompetitionResult;
import PlayMakers.SportsIT.domain.HostProfile;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.MemberType;
import PlayMakers.SportsIT.service.BodyInfoService;
import PlayMakers.SportsIT.service.CompetitionResultService;
import PlayMakers.SportsIT.service.FollowService;
import PlayMakers.SportsIT.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feelit")
public class FeelItController {
    private final MemberService memberService;
    private final FollowService followService;
    private final BodyInfoService bodyInfoService;
    private final CompetitionResultService competitionResultService;

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@AuthenticationPrincipal User user) {
        Map<String, Object> res = new HashMap<>();
        if (user == null) {
            res.put("success", false);
            res.put("message", "로그인이 필요합니다.");
            return ResponseEntity.badRequest().body(res);
        }
        Member member = memberService.findOne(user.getUsername());

        // 공통정보
        res.put("userId", member.getUid());
        res.put("userType", member.getMemberType());
        res.put("name", member.getName());
        res.put("description", member.getDescription());
        res.put("email", member.getEmail());
        res.put("phone", member.getPhone());
        res.put("followers", followService.countByFollowingUid(member.getUid()));
        res.put("followings", followService.countByFollowerUid(member.getUid()));

        res.put("success", true);
        if (member.getMemberType().contains(MemberType.builder().roleName("ROLE_ADMIN").build())) {
            log.info("관리자 로그인");
            res.put("success", false);
            res.put("message", "관리자는 사용할 수 없는 기능입니다.");
            return ResponseEntity.badRequest().body(res);
        } else if (member.getMemberType().contains(MemberType.builder().roleName("ROLE_INSTITUTION").build())) {
            log.info("주최자 로그인");
            HostProfile hostProfile = member.getHostProfile();
            res.put("address", hostProfile.getLocation());
            res.put("latitude", hostProfile.getLatitude());
            res.put("longitude", hostProfile.getLongitude());
            res.put("links", new ArrayList<>() {{
                add(new HashMap<String, Object>() {{
                    put("type", "homepage");
                    put("url", hostProfile.getHomepageUrl());
                    put("imgUrl", "../images/links/home.png");
                }});
                add(new HashMap<String, Object>() {{
                    put("type", "instagram");
                    put("url", hostProfile.getInstagramUrl());
                    put("imgUrl", "../images/links/instagram.png");
                }});
                add(new HashMap<String, Object>() {{
                    put("type", "facebook");
                    put("url", hostProfile.getFacebookUrl());
                    put("imgUrl", "../images/links/facebook.png");
                }});
                add(new HashMap<String, Object>() {{
                    put("type", "naver");
                    put("url", hostProfile.getNaverUrl());
                    put("imgUrl", "../images/links/twitter.png");
                }});
                add(new HashMap<String, Object>() {{
                    put("type", "youtube");
                    put("url", hostProfile.getYoutubeUrl());
                    put("imgUrl", "../images/links/naver.png");
                }});
            }});
            res.put("supervisions", null); // 추후 수정 필요

        } else {
            log.info("체육인 로그인");
            res.put("physical", bodyInfoService.getBodyInfo(member));
            res.put("favorites", null); // 추후 수정 필요
            res.put("career", null); // 추후 수정 필요
            res.put("competitionCareer", new HashMap<String, Object>() {{
                for (CompetitionResult result : member.getCompetitionResults()) {
                    put("title", result.getContent());
                    put("duration", result.getHistoryDate());
                }
            }}
            );}

        return ResponseEntity.ok(res);
    }

}
