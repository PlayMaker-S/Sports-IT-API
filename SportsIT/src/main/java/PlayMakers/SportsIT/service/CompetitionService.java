package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.annotation.MainCompetitionPolicy;
import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.dto.CompetitionDto;
import PlayMakers.SportsIT.exceptions.competition.IllegalMemberTypeException;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CompetitionService {
    private final CompetitionRepository competitionRepository;
    private final MemberRepository memberRepository;
    private final @MainCompetitionPolicy CompetitionPolicy competitionPolicy;

    public Competition create(CompetitionDto dto) {
        // host가 존재하지 않으면 예외 발생
        Member host = memberRepository.findById(dto.getHost().getUid()).orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        // host의 memberType이 ROLE_INSTITUTION 또는 ROLE_ADMIN이 아니면 예외 발생
        if (!host.getMemberType().stream().anyMatch(memberType ->
                        memberType.getRoleName().equals("ROLE_INSTITUTION") ||
                        memberType.getRoleName().equals("ROLE_ADMIN"))) {
            throw new IllegalMemberTypeException("대회 생성 권한이 없습니다.");
        }

        Competition newCompetition = dto.toEntity();

        // 대회 상태 설정 (PLANNING, RECRUITING, RECRUITING_END, IN_PROGRESS) : competition.state == null 일 경우
        if (newCompetition.getState() == null) {
            autoSetCompetitionState(newCompetition);
        }

        // 대회 타입 설정 (FREE, PREMIUM, VIP) : competition.type == null 일 경우
        if (newCompetition.getCompetitionType() == null) {
            newCompetition.setCompetitionType(competitionPolicy.getCompetitionType(host));
        }

        // 필수 정보가 없으면 예외 발생
        checkRequiredInfo(newCompetition);

        // 시간 정보가 유효하지 않으면 예외 발생
        if (newCompetition.getStartDate().isBefore(newCompetition.getRecruitingEnd())) {
            throw new IllegalArgumentException("대회 시작일이 모집 종료일보다 빠릅니다.");
        }
        if (newCompetition.getRecruitingStart().isAfter(newCompetition.getRecruitingEnd())) {
            throw new IllegalArgumentException("모집 종료일이 모집 시작일보다 빠릅니다.");
        }

        log.info("CompetitionService.create() : {}", newCompetition.getState());

        return competitionRepository.save(newCompetition);
    }

    private static void setAutoCompetitionType(Member host, Competition newCompetition) {
        if (host.getSubscription() == Subscribe.BASIC_HOST) newCompetition.setCompetitionType(CompetitionType.FREE);
        else if (host.getSubscription() == Subscribe.PREMIUM_HOST) newCompetition.setCompetitionType(CompetitionType.PREMIUM);
        else if (host.getSubscription() == Subscribe.VIP_HOST) newCompetition.setCompetitionType(CompetitionType.VIP);
        else throw new IllegalArgumentException("회원의 구독 정보가 올바르지 않습니다. 회원 타입 : " + host.getMemberType());
    }

    public static void autoSetCompetitionState(Competition newCompetition) {
        LocalDateTime today = LocalDateTime.now();
        if (today.isBefore(newCompetition.getRecruitingStart())) {
            newCompetition.setState(CompetitionState.PLANNING);
        } else if (today.isBefore(newCompetition.getRecruitingEnd())) {
            newCompetition.setState(CompetitionState.RECRUITING);
        } else if (today.isBefore(newCompetition.getStartDate())) {
            newCompetition.setState(CompetitionState.RECRUITING_END);
        } else {
            newCompetition.setState(CompetitionState.IN_PROGRESS);
        }
    }

    private static void checkRequiredInfo(Competition newCompetition) {
        String errorMessage = "";
        if (newCompetition.getName() == null) errorMessage += "\n대회 이름";
        if (newCompetition.getHost() == null) errorMessage += "\n대회 주최자";
        if (newCompetition.getCategory() == null) errorMessage += "\n대회 종목";
        if (newCompetition.getContent() == null) errorMessage += "\n대회 내용";
        if (newCompetition.getLocation() == null) errorMessage += "\n대회 장소";
        if (newCompetition.getLocationDetail() == null) errorMessage += "\n대회 장소 상세";
        if (newCompetition.getStartDate() == null) errorMessage += "\n대회 시작일";
        if (newCompetition.getRecruitingStart() == null) errorMessage += "\n대회 모집 시작일";
        if (newCompetition.getRecruitingEnd() == null) errorMessage += "\n대회 모집 종료일";
        if (newCompetition.getCompetitionType() == null) errorMessage += "\n대회 프리미엄";
        if (errorMessage.length() > 0) {
            errorMessage = "필수 정보가 없습니다." + errorMessage;
            throw new IllegalArgumentException(errorMessage);
        }
    }

}
