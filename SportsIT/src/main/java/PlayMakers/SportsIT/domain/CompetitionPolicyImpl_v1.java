package PlayMakers.SportsIT.domain;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
//@Primary  // 가장 우선 적용
@Qualifier("mainCompetitionPolicy")
public class CompetitionPolicyImpl_v1 implements CompetitionPolicy{

    @Override
    public CompetitionType getCompetitionType(Member host) {
        if (host.getSubscription() == Subscribe.BASIC_HOST) return CompetitionType.FREE;
        else if (host.getSubscription() == Subscribe.PREMIUM_HOST) return CompetitionType.PREMIUM;
        else if (host.getSubscription() == Subscribe.VIP_HOST) return CompetitionType.VIP;
        else throw new IllegalArgumentException("회원의 구독 정보가 올바르지 않습니다. 회원 타입 : " + host.getMemberType());
    }

    @Override
    public CompetitionState getCompetitionState(Competition competition) {
        LocalDateTime now = LocalDateTime.now();

        return CompetitionState.IN_PROGRESS;
    }
}
