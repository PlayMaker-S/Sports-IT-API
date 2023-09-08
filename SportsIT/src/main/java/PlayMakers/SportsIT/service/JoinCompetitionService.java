package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.dto.*;
import PlayMakers.SportsIT.exceptions.EntityNotFoundException;
import PlayMakers.SportsIT.exceptions.ErrorCode;
import PlayMakers.SportsIT.exceptions.UnAuthorizedException;
import PlayMakers.SportsIT.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JoinCompetitionService {
    final private CompetitionRepository competitionRepository;
    final private CompetitionCustomRepository competitionCustomRepository;
    final private MemberRepository memberRepository;
    final private JoinCompetitionRepository joinCompetitionRepository;
    final private ParticipantRepository participantRepository;

    public JoinCompetition join(JoinCompetitionDto dto) {
        log.info("대회 참가 요청: {}", dto);

        Long uid = dto.getUid();
        Long competitionId = dto.getCompetitionId();

        // 대회 참가 요청을 보낸 회원이 해당 대회에 이미 참가한 회원인지 확인
        if (checkAlreadyJoined(uid, competitionId)) {
            throw new IllegalArgumentException("이미 해당 대회에 참가한 회원입니다.");
        }
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
    public Optional<JoinCompetition> getJoinCompetition(Long uid, Long competitionId){
        log.info("대회 참가 정보 조회 요청: uid={}, competitionId={}", uid, competitionId);

        return joinCompetitionRepository.findByIdUidAndIdCompetitionId(uid, competitionId);
    }

    public List<ParticipantDto.DeleteResponse> deleteJoinCompetition(JoinCompetitionDto dto){
        log.info("대회 참가 정보 삭제 요청: {}", dto);

        // 대회가 이미 시작되었는지 확인
        competitionRepository.findById(dto.getCompetitionId())
                .ifPresent(competition -> {
                    if (isAlreadyStarted(competition)) {
                        throw new IllegalArgumentException("대회가 이미 시작되어 취소/환불이 어렵습니다.");
                    }
                });

        JoinCompetition target = joinCompetitionRepository.findByIdUidAndIdCompetitionId(dto.getUid(), dto.getCompetitionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 대회에 참가하지 않습니다."));

        // 대회 참가 정보 삭제
        List<Participant> participated = participantRepository.findAllByCompetitionCompetitionIdAndMemberUid(dto.getCompetitionId(), dto.getUid());
        List<ParticipantDto.DeleteResponse> deleted = new ArrayList<>();
        for (Participant participant : participated) {
            deleted.add(ParticipantDto.DeleteResponse.builder()
                    .competitionName(participant.getCompetition().getName())
                    .sectorTitle(participant.getId().getSectorTitle())
                    .subSectorName(participant.getId().getSubSectorName())
                    .build());
        }
        participantRepository.deleteAll(participated);

        joinCompetitionRepository.delete(target);

        return deleted;
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

    public boolean checkAlreadyJoined(Long uid, Long competitionId) {
        JoinCompetition find = joinCompetitionRepository.findByIdUidAndIdCompetitionId(uid, competitionId).orElse(null);
        log.info("이미 참가한 대회인지 확인: {}", find);
        if (find != null) {
            return true;
        }
        return false;
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
        if (competition == null) throw new EntityNotFoundException(ErrorCode.COMPETITION_NOT_FOUND, "대회를 찾을 수 없습니다. : ");
        // ROLE_PLAYER가 아니라면 예외 발생
        if (isNotPlayer(member)) {
            throw new UnAuthorizedException(ErrorCode.NOT_PLAYER, "대회 참가 권한이 없는 회원입니다.");
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

    public Slice<JoinCompetitionDto.UserJoinResponse> findJoinedCompetitionsByUid(Long uid, Long page, Long size) {
        List<JoinCompetition> joins = joinCompetitionRepository.findByIdUid(uid);
        joins.sort(Comparator.comparing(JoinCompetition::getCreatedDate).reversed());

        List<Long> competitionIds = joins.stream().map(join -> join.getId().getCompetitionId()).toList();

        Pageable pageable = PageRequest.of(page.intValue(), size.intValue(), Sort.by("startDate").descending());
        Slice<Competition> competitions = competitionCustomRepository.findCompetitionsBySliceWithIdsAndUid(competitionIds, uid, pageable);

        List<JoinCompetitionDto.UserJoinResponse> result = new ArrayList<>();
        for (JoinCompetition join : joins) {
            result.add(JoinCompetitionDto.UserJoinResponse.builder()
                    .competition(getCompetitionSummary(join.getCompetition()))
                    .type(join.getJoinType())
                    .joinDate(join.getCreatedDate())
                    .build());
        }
        return new SliceImpl<>(result, pageable, competitions.hasNext());
    }
    public List<Member> getJoinedMembersByCompetition(Competition competition){
        List<JoinCompetition> joinCompetitions = joinCompetitionRepository.findByIdCompetitionId(competition.getCompetitionId());
        List<Member> members = new ArrayList<>();
        for(JoinCompetition joinCompetition : joinCompetitions){
            members.add(joinCompetition.getMember());
        }
        return members;
    }

    private static CompetitionDto.Summary getCompetitionSummary(Competition competition) {
        return CompetitionDto.Summary.builder()
                .competitionId(competition.getCompetitionId())
                .name(competition.getName())
                .host(MemberDto.Summary.builder()
                        .uid(competition.getHost().getUid())
                        .name(competition.getHost().getName())
                        .build())
                .posters(competition.getPosters())
                .startDate(competition.getStartDate())
                .sportCategory(competition.getCategory())
                .build();
    }
}
