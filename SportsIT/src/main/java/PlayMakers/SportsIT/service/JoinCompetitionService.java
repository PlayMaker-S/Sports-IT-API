package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.JoinCompetition;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.dto.JoinCompetitionDto;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.JoinCompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JoinCompetitionService {
    final private CompetitionRepository competitionRepository;
    final private MemberRepository memberRepository;
    final private JoinCompetitionRepository joinCompetitionRepository;

    public JoinCompetition join(JoinCompetitionDto dto) {
        log.info("대회 참가 요청: {}", dto);

        // 대회 참가 요청을 보낸 회원이 해당 대회에 이미 참가한 회원인지 확인
        checkAlreadyJoined(dto);
        // 대회 참가 요청을 보낸 회원이 해당 대회에 참가할 수 있는 회원인지 확인
        checkPlayer(dto);
        // 대회 참가 요청을 받은 대회가 신청 가능한지 확인
        checkJoinable(dto);

        JoinCompetition join = dto.toEntity();
        join.setMember(memberRepository.findById(dto.getUid()).get());
        join.setCompetition(competitionRepository.findById(dto.getCompetitionId()).get());
        log.info("대회 참가 정보: {}", join);

        return joinCompetitionRepository.save(join);
    }
    public JoinCompetition updateJoinCompetition(JoinCompetitionDto dto){
        log.info("대회 참가 정보 수정 요청: {}", dto);

        // 대회 참가서 수정 요청을 받은 대회가 신청 가능지 확인
        checkJoinable(dto);

        JoinCompetition target = joinCompetitionRepository.findByIdUidAndIdCompetitionId(dto.getUid(), dto.getCompetitionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 대회에 참가한 회원이 존재하지 않습니다."));

        target.update(dto.getType(), dto.getFormId());

        return joinCompetitionRepository.save(target);
    }

    public void deleteJoinCompetition(JoinCompetitionDto dto){
        log.info("대회 참가 정보 삭제 요청: {}", dto);

        // 대회가 이미 시작되었는지 확인
        competitionRepository.findById(dto.getCompetitionId())
                .ifPresent(competition -> {
                    if (isAlreadyStarted(competition)) {
                        throw new IllegalArgumentException("대회가 이미 시작되어 취소/환불이 어렵습니다.");
                    }
                });

        JoinCompetition target = joinCompetitionRepository.findByIdUidAndIdCompetitionId(dto.getUid(), dto.getCompetitionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 대회에 참가한 회원이 존재하지 않습니다."));

        joinCompetitionRepository.delete(target);
    }

    public void checkJoinable(JoinCompetitionDto dto) {
        competitionRepository.findById(dto.getCompetitionId())
                .ifPresent(competition -> {
                    if (isJoinableToday(competition)) {
                        throw new IllegalArgumentException("대회 참가/관람 신청 기간이 아닙니다.");
                    }
                    if (isAlreadyFull(dto, competition)) {
                        if(dto.getType().equals(JoinCompetition.joinType.PLAYER)) throw new IllegalArgumentException("대회 참가 인원이 마감되었습니다.");
                        else throw new IllegalArgumentException("대회 관람 인원이 마감되었습니다.");
                    }
                });
    }

    private static boolean isAlreadyStarted(Competition competition) {
        return competition.getStartDate().isBefore(LocalDateTime.now());
    }

    private void checkPlayer(JoinCompetitionDto dto) {
        memberRepository.findById(dto.getUid())
                .ifPresent(member -> {
                    // 회원이 대회에 참가할 수 있는 회원인지 확인
                    if (isPlayer(member)) {
                        throw new IllegalArgumentException("대회 참가 권한이 없는 회원입니다.");
                    }
                });
    }

    private void checkAlreadyJoined(JoinCompetitionDto dto) {
        joinCompetitionRepository.findByIdUidAndIdCompetitionId(dto.getUid(), dto.getCompetitionId())
                .ifPresent(joinCompetition -> {
                    throw new IllegalArgumentException("이미 해당 대회에 신청한 회원입니다.");
                });
    }

    private static boolean isPlayer(Member member) {
        return member.getMemberType().stream().anyMatch(memberType ->
                memberType.getRoleName().equals("ROLE_PLAYER") ||
                        memberType.getRoleName().equals("ROLE_INSTITUTION"));
    }

    private static boolean isJoinableToday(Competition competition) {
        return LocalDateTime.now().isBefore(competition.getRecruitingStart().minusSeconds(1)) &&
                LocalDateTime.now().isAfter(competition.getRecruitingEnd().plusSeconds(1));
    }

    private boolean isAlreadyFull(JoinCompetitionDto dto, Competition competition) {
        return joinCompetitionRepository.countByIdCompetitionIdAndJoinType(competition.getCompetitionId(), dto.getType()) >= competition.getMaxViewer();
    }
}
