package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.CompetitionState;
import PlayMakers.SportsIT.domain.JoinCompetition;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.dto.JoinCompetitionDto;
import PlayMakers.SportsIT.dto.JoinCountDto;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.JoinCompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

        Long uid = dto.getUid();
        Long competitionId = dto.getCompetitionId();

        // 대회 참가 요청을 보낸 회원이 해당 대회에 이미 참가한 회원인지 확인
        checkAlreadyJoined(uid, competitionId);
        // 대회 참가 요청을 보낸 회원이 해당 대회에 참가할 수 있는 회원인지 확인
        checkPlayer(uid);
        // 대회 참가 요청을 받은 대회가 신청 가능한지 확인
        checkJoinable(competitionId, dto.getType());

        JoinCompetition join = dto.toEntity();
        join.setMember(memberRepository.findById(dto.getUid()).get());
        join.setCompetition(competitionRepository.findById(dto.getCompetitionId()).get());
        log.info("대회 참가 정보: {}", join);

        return joinCompetitionRepository.save(join);
    }
    public JoinCompetition updateJoinCompetition(JoinCompetitionDto dto){
        log.info("대회 참가 정보 수정 요청: {}", dto);

        // 대회 참가서 수정 요청을 받은 대회가 신청 가능지 확인
        checkJoinable(dto.getCompetitionId(), dto.getType());

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

    public void checkJoinable(Long competitionId, JoinCompetition.joinType type) {
        competitionRepository.findById(competitionId)
                .ifPresent(competition -> {
                    if (!isJoinableToday(competition)) {
                        throw new IllegalArgumentException("대회 참가/관람 신청 기간이 아닙니다.\n" +
                                "대회 신청 기간: " + competition.getRecruitingStart().toLocalDate() + " " + competition.getRecruitingStart().toLocalTime() +
                                " ~ " + competition.getRecruitingEnd().toLocalDate() + " " + competition.getRecruitingEnd().toLocalTime());
                    }
                    if (isAlreadyFull(competition, type)) {
                        if(type.equals(JoinCompetition.joinType.PLAYER)) throw new IllegalArgumentException("대회 참가 인원이 마감되었습니다.");
                        else throw new IllegalArgumentException("대회 관람 인원이 마감되었습니다.");
                    }
                });
    }

    public JoinCountDto countJoinCompetition(Long competitionId){
        Competition competition = competitionRepository.findByCompetitionId(competitionId);
        JoinCountDto countResult = JoinCountDto.builder()
                .maxPlayerCount(competition.getMaxPlayer())
                .playerCount(countCurrentPlayer(competitionId))
                .maxViewerCount(competition.getMaxViewer())
                .viewerCount(countCurrentViewer(competitionId))
                .build();
        return countResult;
    }
    private int countCurrentPlayer(Long competitionId){
        return joinCompetitionRepository.countByIdCompetitionIdAndJoinType(competitionId, JoinCompetition.joinType.PLAYER);
    }
    private int countCurrentViewer(Long competitionId){
        return joinCompetitionRepository.countByIdCompetitionIdAndJoinType(competitionId, JoinCompetition.joinType.VIEWER);
    }

    private static boolean isAlreadyStarted(Competition competition) {
        return competition.getStartDate().isBefore(LocalDateTime.now());
    }

    private void checkPlayer(Long uid) {
        memberRepository.findById(uid)
                .ifPresent(member -> {
                    // 회원이 대회에 참가할 수 있는 회원인지 확인
                    if (isNotPlayer(member)) {
                        throw new IllegalArgumentException("대회 참가 권한이 없는 회원입니다.");
                    }
                });
    }

    public void checkAlreadyJoined(Long uid, Long competitionId) {
        joinCompetitionRepository.findByIdUidAndIdCompetitionId(uid, competitionId)
                .ifPresent(joinCompetition -> {
                    throw new IllegalArgumentException("이미 해당 대회에 신청한 회원입니다.");
                });
    }

    private static boolean isNotPlayer(Member member) {
        return member.getMemberType().stream().anyMatch(memberType ->
                memberType.getRoleName().equals("ROLE_ADMIN") ||
                        memberType.getRoleName().equals("ROLE_INSTITUTION"));
    }

    public static boolean isJoinableToday(Competition competition) {
        return competition.getState().equals(CompetitionState.RECRUITING);
    }

    public boolean isAlreadyFull(Competition competition, JoinCompetition.joinType type) {
        return joinCompetitionRepository.countByIdCompetitionIdAndJoinType(competition.getCompetitionId(), type) >= competition.getMaxViewer();
    }

    public Map<String, String> getJoinCounts(Long competitionId, Member member) throws Exception {
        Competition competition = competitionRepository.findByCompetitionId(competitionId);
        // ROLE_PLAYER가 아니라면 예외 발생
        if (isNotPlayer(member)) {
            throw new IllegalAccessException("대회 참가 권한이 없는 회원입니다.");
        }
        // 이미 참가한 대회라면 예외 발생
        if (alreadyJoinedPlayer(competitionId, member)) {
            throw new IllegalAccessException("이미 참가한 대회입니다.");
        }
        // 현재 대회가 모집중이 아니라면 예외 발생
        if (!competition.getState().equals(CompetitionState.RECRUITING)) {
            throw new IllegalAccessException("대회 모집 기간이 아닙니다.");
        }

        String availablePlayer, availableViewer;
        Integer maxPlayer = competition.getMaxPlayer();
        Integer maxViewer = competition.getMaxViewer();


        if (maxPlayer != null) {
            availablePlayer = String.valueOf(maxPlayer - countCurrentPlayer(competitionId));
        } else {
            availablePlayer = "참석 가능";
        }
        if (maxViewer != null) {
            availableViewer = String.valueOf(maxViewer - countCurrentViewer(competitionId));
        } else {
            availableViewer = "참석 가능";
        }

        return new HashMap<>() {{
            put("availablePlayer", availablePlayer);
            put("availableViewer", availableViewer);
        }};
    }

    private boolean alreadyJoinedPlayer(Long competitionId, Member member) {
        return joinCompetitionRepository.findByIdUidAndIdCompetitionId(member.getUid(), competitionId).isPresent();
    }
}
