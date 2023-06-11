package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.Follow;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.repository.FollowRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    public Long countByFollowerUid(Long followerId) {
        return followRepository.countByFollowerUid(followerId);
    }

    public Long countByFollowingUid(Long followingId) {
        return followRepository.countByFollowingUid(followingId);
    }

    // follower가 following을 follow
    public void follow(Long followerId, Long followingId) {
        Member follower = memberRepository.findById(followerId).get();
        Member following = memberRepository.findById(followingId).get();
        followRepository.save(Follow.builder().follower(follower).following(following).build());
    }
    public void unfollow(Long followerId, Long followingId) {
        Member follower = memberRepository.findById(followerId).get();
        Member following = memberRepository.findById(followingId).get();
        followRepository.delete(Follow.builder().follower(follower).following(following).build());
    }

}
