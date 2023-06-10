package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.dto.*;
import PlayMakers.SportsIT.service.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static java.lang.Integer.parseInt;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {
    private final CompetitionService competitionService;
    private final MemberService memberService;
    private final JoinCompetitionService joinCompetitionService;
    private final CompetitionTemplateService competitionTemplateService;
    private final AgreementService agreementService;
    private final CompetitionFormService competitionFormService;
    private final ParticipantService participantService;

    /*
        대회 생성
     */
    /**
     * 대회 생성 컨트롤러
     * @param dto
     * @param user
     * @return success: true, result: 생성한 Competition
     * @throws Exception 로그인 에러, 주최자가 아닌 경우
     */
    @PostMapping
    public ResponseEntity<?> createCompetition(@RequestBody CompetitionDto dto,
                                               @AuthenticationPrincipal User user) throws Exception {
        // 주최자 ID 설정 - 일단 dto에 memberId가 포함된다고 가정
        String hostEmail = null;
        try {
            hostEmail = user.getUsername(); // 로그인한 회원 ID를 가져옴
        } catch (Exception e) {
            throw new EntityNotFoundException("로그인이 필요합니다.");
        }

        Member host = memberService.findOne(hostEmail);
        dto.setHost(host);

        // 대회 생성
        Competition competition = competitionService.create(dto);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("result", competition);

        return ResponseEntity.created(URI.create("/api/competitions/" + competition.getCompetitionId())) // Location Header에 생성된 리소스의 URI를 담아서 보냄
                .body(res); // 201
    }

    /*
        대회 조회, 수정, 요청
     */

    /**
     * 모든 대회 조회 컨트롤러
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity<Optional<Competition>> getCompetitions() {
        //List<Competition> competitions = competitionService.getCompetitions();
        Optional<Competition> competitions = null;
        return ResponseEntity.ok(competitions); // 200
    }

    /**
     * @param keyword
     * @param filteringConditions
     * @param orderBy
     * @param page
     * @param size
     * @return ResponseEntity<?> - 200 : success: true, result: Slice<Competition>
     */
    @GetMapping("/slice")
    public ResponseEntity<Slice<Competition>> getCompetitionSlice(@RequestParam(required = false) String keyword,
                                                                  @RequestParam(value = "filterBy", required = false) List<String> filteringConditions,
                                                                  @RequestParam String orderBy,
                                                                  @RequestParam String page,
                                                                  @RequestParam String size) {
        log.info("대회 slice 요청: {} {} {} {} {}", keyword, filteringConditions, orderBy, page, size);

        Slice<Competition> competitions = competitionService.getCompetitionSlice(keyword, filteringConditions, orderBy, parseInt(page), parseInt(size));
        return ResponseEntity.ok(competitions); // 200
    }

    @GetMapping("/{competitionId}")
    public ResponseEntity<Competition> getCompetition(@PathVariable Long competitionId) {
        Competition competition = competitionService.findById(competitionId);
        return ResponseEntity.ok(competition); // 200
    }

    @PutMapping("/{competitionId}")
    public ResponseEntity<Competition> updateCompetition(@PathVariable Long competitionId, @RequestBody CompetitionDto dto) {
        Competition competition = competitionService.update(competitionId, dto);
        return ResponseEntity.ok(competition); // 200
    }

    @DeleteMapping("/{competitionId}")
    public ResponseEntity<Void> deleteCompetition(@PathVariable Long competitionId) {
        competitionService.delete(competitionId);
        return ResponseEntity.noContent().build(); // 204
    }
    /*
        대회 참가
     */

    /**
     * 대회 참가시 참가 가능한 인원 수를 반환, 대회 참가가 불가능할 경우 예외 발생
     * @param competitionId
     * @param user
     * @return
     * @throws Exception
     */
    @GetMapping("/{competitionId}/join/init")
    public ResponseEntity<Object> initJoin(@PathVariable Long competitionId,
                                           @AuthenticationPrincipal User user) throws Exception{
        Member member = getMember(user);

        Map<String, Object> res = new HashMap<>();
        try {
            Map<String, String> result = joinCompetitionService.getJoinCounts(competitionId, member);
            res.put("success", true);
            res.put("result", result);

            return ResponseEntity.ok(res); // 200
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res); // 400
        }

    }

    /**
     * 대회 참가시 참가 타입에 맞는 포맷 반환, 대회 참가가 불가능할 경우 예외 발생
     * @param competitionId
     * @param joinType
     * @param user
     * @return success: true, agreements: 규약, template: 신청서 템플릿
     * @throws Exception
     */
    @GetMapping("/{competitionId}/join/format")
    public ResponseEntity<Object> getCompetitionContents(@PathVariable Long competitionId,
                                                         @RequestParam String joinType,
                                                         @AuthenticationPrincipal User user) throws Exception{
        Member member = getMember(user);

        Map<String, Object> res = new HashMap<>();

        if (joinCompetitionService.checkAlreadyJoined(member.getUid(), competitionId)) {
            res.put("success", false);
            res.put("message", "이미 참가한 대회입니다.");
            return ResponseEntity.ok(res); // 200
        }
        try{
            JoinCompetition.joinType type = getJoinType(joinType);
            joinCompetitionService.checkJoinable(competitionId, JoinCompetition.joinType.PLAYER);
        } catch (IllegalArgumentException e) {
            res.put("success", false);
            res.put("message", e.getMessage());
            return ResponseEntity.ok(res); // 200
        }
        Competition target = competitionService.findById(competitionId);
        res.put("success", true);
        res.put("agreements", target.getAgreements());
        String templateId = target.getTemplateID();
        res.put("template", competitionTemplateService.getTemplate(templateId));

        return ResponseEntity.ok(res); // 200
    }

    /**
     * 선택한 부문과 체급에 맞는 대회 참가 신청서를 반환
     * @param competitionId
     * @param joinType
     * @param formDto
     * @param user
     * @return
     * @throws Exception
     */
    @PostMapping("/{competitionId}/join/format")
    public ResponseEntity<Object> postJoinForm(@PathVariable Long competitionId,
                                               @RequestParam String joinType,
                                               @RequestBody CompetitionFormDto formDto,
                                               @AuthenticationPrincipal User user) throws Exception{
        Member member = getMember(user);
        Competition target = competitionService.findById(competitionId);
        Map<String, Object> res = new HashMap<>();

        if (joinCompetitionService.checkAlreadyJoined(member.getUid(), competitionId)) {
            res.put("success", false);
            res.put("message", "이미 참가한 대회입니다.");
            return ResponseEntity.ok(res); // 200
        }
        JoinCompetition.joinType type = getJoinType(joinType);
        try{
            joinCompetitionService.checkJoinable(competitionId, type);
        } catch (IllegalArgumentException e) {
            res.put("success", false);
            res.put("message", e.getMessage());
            return ResponseEntity.ok(res); // 200
        }
        // 결제 금액 계산
        String templateId = target.getTemplateID();
        CompetitionTemplate template = competitionTemplateService.getTemplate(templateId);
        Long amount = competitionService.calculatePrice(template, formDto);

        String formId = competitionFormService.createForm(formDto.toEntity());

        res.put("success", true);
        res.put("amount", amount);
        res.put("form", formId); // 신청서 저장

        return ResponseEntity.ok(res); // 200
    }

    private Member getMember(User user) {
        Member member = memberService.findOne(user.getUsername());
        return member;
    }

    private static JoinCompetition.joinType getJoinType(String joinType) {
        JoinCompetition.joinType type = null;
        if (joinType.equals("player")) {
            return JoinCompetition.joinType.PLAYER;
        } else if (joinType.equals("viewer")) {
            return JoinCompetition.joinType.VIEWER;
        } else {
            throw new IllegalArgumentException("joinType은 player 또는 viewer이어야 합니다.");
        }
    }
    @GetMapping("/{competitionId}/participants")
    public ResponseEntity<Object> loadParticipants(@PathVariable Long competitionId) throws Exception{
        Map<String, Object> res = new HashMap<>();
        List<ParticipantDto.Response> participants;
        try {
            participants = participantService.findAllDtoByCompetitionId(competitionId);
            res.put("success", true);
            res.put("result", participants);
            return ResponseEntity.ok(res); // 200
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res); // 400
        }
    }

    @GetMapping("/{competitionId}/join/player/agreements")
    public ResponseEntity<Object> getAgreements(@PathVariable Long competitionId) throws Exception{
        Map<String, Object> res = new HashMap<>();
        try {
            List<Agreement> agreements = agreementService.findAgreementsByCompetition(competitionService.findById(competitionId));
            res.put("success", true);
            res.put("result", agreements);

            return ResponseEntity.ok(res); // 200
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res); // 400
        }
    }

    @GetMapping("/join/{competitionId}/viewer/checkJoinable")
    public ResponseEntity<String> isJoinableViewer(@PathVariable Long competitionId,
                                             @AuthenticationPrincipal User user) throws Exception{
        Member member = getMember(user);

        joinCompetitionService.checkAlreadyJoined(member.getUid(), competitionId);
        joinCompetitionService.checkJoinable(competitionId, JoinCompetition.joinType.VIEWER);

        return ResponseEntity.ok("success"); // 200
    }

    @PostMapping("/{competitionId}/join")
    public ResponseEntity<Object> joinCompetition(@RequestBody JoinCompetitionDto joinCompetitionDto,
                                                  @RequestParam String joinType,
                                                  @PathVariable Long competitionId,
                                                  @AuthenticationPrincipal User user) throws Exception{

        Map<String, Object> res = new HashMap<>();

        log.info("대회 참가 요청 Controller: {}", joinCompetitionDto);
        Member member = getMember(user);
        joinCompetitionDto.setUid(member.getUid());
        Competition competition = competitionService.findById(competitionId);
        joinCompetitionDto.setCompetitionId(competitionId);
        if (joinType.equals("viewer")) {
            joinCompetitionDto.setType(JoinCompetition.joinType.VIEWER);
        } else {
            joinCompetitionDto.setType(JoinCompetition.joinType.PLAYER);
        }
        JoinCompetition joinCompetition = joinCompetitionService.join(joinCompetitionDto);

        List<Participant> participants = null;
        List<ParticipantDto.Response> participantsDto = new ArrayList<>();

        // Participants 객체 생성
        if(joinType.equals("player")){
            try {
                CompetitionForm form = competitionFormService.getForm(joinCompetition.getFormId());
                log.info("참가자 신청서: {}", form);
                participants = participantService.parseAndSaveParticipants(member, competition, form);
                for (Participant participant: participants) {
                    participantsDto.add(ParticipantDto.Response.builder()
                            .uid(participant.getId().getUid())
                            .userName(participant.getMember().getName())
                            .sectorTitle(participant.getId().getSectorTitle())
                            .subSectorName(participant.getId().getSubSectorName())
                            .build());
                }
            } catch (Exception e) {
                String errMessage = "참가자 신청서 불러오기 실패";
                log.error(errMessage);
                res.put("success", false);
                res.put("message", errMessage);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res); // 400
            }

        }
        res.put("success", true);
        res.put("joinCompetition", joinCompetitionDto);
        res.put("participants", participantsDto);

        // 생성된 리소스의 uri와 함께 201코드, "success" 응답
        return ResponseEntity.created(URI.create("/" + joinCompetition.getId())) // Location Header에 생성된 리소스의 URI를 담아서 보냄
                .body(res); // 201
    }
    @DeleteMapping("/join")
    public ResponseEntity<String> cancelJoinCompetition(@RequestBody JoinCompetitionDto joincompetitionDto) throws Exception{
        joinCompetitionService.deleteJoinCompetition(joincompetitionDto);

        return ResponseEntity.accepted().body("대회가 취소되었습니다."); // 202
    }
    @GetMapping("/join/slice/{userId}")
    public ResponseEntity<Object> getJoinCompetitionSlice(@PathVariable Long userId,
                                                                      @RequestParam(required = false) Long page,
                                                                      @RequestParam(required = false) Long size) throws Exception {
        Map<String, Object> res = new HashMap<>();
        Slice<JoinCompetitionDto.UserJoinResponse> result = null;
        page = page == null ? 0 : page;
        size = size == null ? 15 : size;
        try {
            result =  joinCompetitionService.findJoinedCompetitions(userId, page, size);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
        res.put("success", true);
        res.put("result", result);

        return ResponseEntity.ok(res); // 200
    }

    @GetMapping("/count-join/{competitionId}")
    public ResponseEntity<JoinCountDto> getJoinCompetitionCounts(@PathVariable Long competitionId) throws Exception {
        JoinCountDto result =  joinCompetitionService.countJoinCompetition(competitionId);
        return ResponseEntity.ok(result); // 200
    }

    @PostMapping("/firebase-test")
    public ResponseEntity<String> testFirebase(@RequestBody CompetitionTemplate template) throws Exception {
        log.info("템플릿 생성 요청 Controller: {}", template);
        String docId = competitionTemplateService.saveTemplate(template);
        return ResponseEntity.ok(docId); // 200
    }

    /*
        템플릿 관련 API
     */

    @PostMapping("/template")
    public ResponseEntity<Object> createTemplate(@RequestBody CompetitionTemplate template) throws Exception {
        log.info("템플릿 생성 요청 Controller: {}", template);
        String docId = competitionTemplateService.saveTemplate(template);
        Object result = new HashMap<String, Object>() {{
            put("success", true);
            put("templateId", docId);
        }};
        return ResponseEntity.created(URI.create("/api/competitions/template/" + docId)) // Location Header에 생성된 리소스의 URI를 담아서 보냄
                .body(result); // 201
    }
    @PutMapping("/template/{templateId}")
    public ResponseEntity<?> updateTemplate(@PathVariable String templateId, @RequestBody CompetitionTemplate template) throws Exception {
        log.info("템플릿 수정 요청 Controller: {}", template);
        competitionTemplateService.updateTemplate(templateId, template);
        Object result = new HashMap<String, Object>() {{
            put("success", true);
        }};
        return ResponseEntity.ok(result); // 200
    }
    @GetMapping("/template/{templateId}")
    public ResponseEntity<?> getTemplate(@PathVariable String templateId) throws Exception {
        log.info("템플릿 조회 요청 Controller: {}", templateId);
        CompetitionTemplate template = competitionTemplateService.getTemplate(templateId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("result", template);
        return ResponseEntity.ok(result); // 200
    }
    @DeleteMapping("/template/{templateId}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String templateId) throws Exception {
        log.info("템플릿 삭제 요청 Controller: {}", templateId);
        competitionTemplateService.deleteTemplate(templateId);
        return ResponseEntity.noContent().build(); // 204
    }
    @GetMapping("/form/{formId}")
    public ResponseEntity<?> getForm(@PathVariable String formId) throws Exception {
        log.info("템플릿 조회 요청 Controller: {}", formId);
        CompetitionForm form = competitionFormService.getForm(formId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("result", form);
        return ResponseEntity.ok(result); // 200
    }
    @GetMapping("/result/{competitionId}")
    public ResponseEntity<List<CompetitionResultDto>> getCompetitionResult(@PathVariable Long competitionId){
        log.info("대회 결과 조회");
        return ResponseEntity.ok(competitionService.getAllResultsByCompetition(competitionId));
    }


    /*
        예외 처리
     */


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleNoSuchElementFoundException(EntityNotFoundException exception) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", false);
        res.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res); // 404
    }

    @ExceptionHandler({ExecutionException.class, InterruptedException.class})
    public ResponseEntity<?> handleFirestoreException(Exception exception) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", false);
        res.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res); // 500
    }
    @ExceptionHandler
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage()); // 400
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage()); // 500
    }
}
