package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.dto.*;
import PlayMakers.SportsIT.exceptions.ErrorCode;
import PlayMakers.SportsIT.exceptions.UnAuthorizedException;
import PlayMakers.SportsIT.service.*;
import PlayMakers.SportsIT.utils.api.ApiUtils;
import PlayMakers.SportsIT.utils.api.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static java.lang.Integer.parseInt;
import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@Tag(name = "2. 대회 API", description = "\uD83D\uDCAA 대회 생성, 조회, 수정, 취소 및 대회 참가 신청/취소 관련 API 목록입니다.")
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
    @Operation(summary = "대회 생성 API", description = """
            \uD83D\uDCCC 클라이언트에서 대회 정보와 사용자 토큰 정보를 받아 대회를 생성합니다. 토큰 값이 필요합니다. 생성된 대회는 "location" 헤더에서 확인할 수 있습니다.(location:/api/competitions/{competitionId})\n\n
            ✔️ 성공시 success: true를 반환합니다. (201)\n\n
            ❌ 실패시 HTTP Status Code와 에러 코드를 반환합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "대회 생성 성공", content = @Content(schema = @Schema(ref = "#/components/schemas/PostResponse"))),
            @ApiResponse(responseCode = "400", description = "(COMPETITION-001) 대회 정보가 누락되거나 잘못되었을 경우", content = @Content),
            @ApiResponse(responseCode = "401", description = "(AUTH-001) Access Token이 비어있거나 잘못 되었을 경우", content = @Content),
            @ApiResponse(responseCode = "403", description = "(AUTH-003) 주최자 계정이 아닐 경우", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CommonResponse<Object>> createCompetition(
            @Parameter(name = "competitionDto", description = "대회 정보 객체", required = true)
            @RequestBody CompetitionDto.Form dto,
            @AuthenticationPrincipal User user) throws Exception {
        // 주최자 ID 설정 - 일단 dto에 memberId가 포함된다고 가정
        String hostEmail = null;
        Member host;
        try {
            host = getMember(user);
        } catch (BadCredentialsException e) {
            log.error("로그인 에러");
            throw new PlayMakers.SportsIT.exceptions.EntityNotFoundException(ErrorCode.EMPTY_TOKEN, "Access Token이 비어있거나 잘못 되었습니다.");
        } catch (EntityNotFoundException e) {
            log.error("User Not Found : {}", hostEmail);
            throw new PlayMakers.SportsIT.exceptions.EntityNotFoundException(ErrorCode.USER_NOT_FOUND, "로그인한 사용자가 존재하지 않습니다.");
        }

        /*
        CompetitionDto -> CompetitionDto.Form으로 동작하도록 Refactoring 필요
        2023.08.29 - MyeongQ
        * */
        CompetitionDto temp = dto.toAllArgsDto();
        temp.setHost(host);

        // 대회 생성
        Competition competition = competitionService.create(temp);
        //CommonResponse<Object> res = new CommonResponse<>(CREATED.value(), true, null);
        CommonResponse<Object> res = ApiUtils.success(CREATED.value(), null);

        return ResponseEntity.created(URI.create("/api/competitions/" + competition.getCompetitionId())) // Location Header에 생성된 리소스의 URI를 담아서 보냄
                .body(res); // 201
    }

    private static String getUserEmailFromAuthenticationToken(User user) {
        try {
            return user.getUsername();
        } catch (Exception e) {
            throw new PlayMakers.SportsIT.exceptions.EntityNotFoundException(ErrorCode.EMPTY_TOKEN);
        }
    }

    /*
        대회 조회, 수정, 요청
     */

    /**
     * 모든 대회 조회 컨트롤러
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity<List<Competition>> getCompetitions() {
        List<Competition> competitions = competitionService.findAll();
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

    /**
     * 대회 상세 조회 컨트롤러
     * @param competitionId Long
     * @param user User
     * @return ResponseEntity
     */
    @Operation(summary = "대회 상세 조회 API", description = """
            \uD83D\uDCCC 대회 ID로 대회 정보를 조회합니다. 참가 여부, 스크랩 여부를 함께 확인하려면 토큰 값이 필요합니다.\n\n
            ✔️ 성공시 생성한 대회 정보(result)와 success: true를 반환합니다. (200)\n\n
            ❌ 실패시 HTTP Status Code와 에러 코드를 반환합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대회 조회 성공", content = @Content(schema = @Schema(implementation = CompetitionDto.Info.class))),
            @ApiResponse(responseCode = "404", description = "(COMPETITION-003) 해당 ID의 대회가 존재하지 않을 경우", content = @Content)
    })
    @GetMapping("/{competitionId}")
    public ResponseEntity<CommonResponse<CompetitionDto.Info>> getCompetition(
            @Parameter(name = "competitionId", description = "대회 ID", required = true, in = ParameterIn.PATH)
            @PathVariable Long competitionId,
            @AuthenticationPrincipal User user) throws Exception {
        Competition competition = competitionService.findById(competitionId);
        boolean joined = (user != null) && joinCompetitionService.checkAlreadyJoined(memberService.findOne(getUserEmailFromAuthenticationToken(user)).getUid(), competitionId);

        CompetitionDto.Info dto = CompetitionDto.Info.entityToInfo(competition);
        dto.setJoined(joined);

        CommonResponse<CompetitionDto.Info> res = ApiUtils.success(HttpStatus.OK.value(), dto);

        return ResponseEntity.ok(res); // 200
    }

    /**
     * 대회 수정 컨트롤러
     * @param competitionId Long
     * @param dto CompetitionDto.Form
     * @return
     */
    @Operation(summary = "대회 수정 API", description = """
            \uD83D\uDCCC 대회 ID로 대회 정보를 수정합니다.\n\n
            ✔️ 성공시 success: true를 반환합니다. (201)\n\n
            ❌ 실패시 success: false를 반환합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "대회 수정 성공", content = @Content(schema = @Schema(ref = "#/components/schemas/PostResponse"))),
            @ApiResponse(responseCode = "401", description = "(AUTH-001) Token이 비어있는 경우", content = @Content),
            @ApiResponse(responseCode = "403", description = "(COMMON-004) 로그인한 사용자가 관리자나 해당 대회를 게시한 사용자가 아닐 경우\n\n" +
                                                             "(AUTH-004) 대회가 이미 시작되었거나 종료된 경우", content = @Content),
            @ApiResponse(responseCode = "404", description = "(COMPETITION-003) 해당 ID의 대회가 존재하지 않을 경우", content = @Content)
    })
    @PutMapping("/{competitionId}")
    public ResponseEntity<Object> updateCompetition(
            @Parameter(name = "competitionId", description = "대회 ID", required = true, in = ParameterIn.PATH)
            @PathVariable Long competitionId,
            @Parameter(name = "competitionDto", description = "대회 입력 객체", required = true)
            @RequestBody CompetitionDto.Form dto,
            @AuthenticationPrincipal User user) throws Exception {

        Competition competition = competitionService.findById(competitionId);
        Member host = getMember(user);
        log.info("대회 수정 요청: {}", host.getUid());
        if (!competition.getHost().getUid().equals(host.getUid()) && host.getMemberType().stream().noneMatch(
                memberType -> memberType.getRoleName().equals("ROLE_ADMIN"))) {
            throw new UnAuthorizedException(ErrorCode.ACCESS_DENIED, "관리자 또는 작성자 본인만 대회 정보를 수정할 수 있습니다.");
        }

        competitionService.checkCompetitionNotStarted(competition);

        competitionService.update(competitionId, dto);

        return ResponseEntity.created(URI.create("/api/competitions/" + competition.getCompetitionId()))
                .body(ApiUtils.created(HttpStatus.NO_CONTENT.value())); // 201
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
        Member member = memberService.findOne(getUserEmailFromAuthenticationToken(user));
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
    public ResponseEntity<Object> cancelJoinCompetition(@RequestBody JoinCompetitionDto joincompetitionDto) throws Exception{
        Map<String, Object> res = new HashMap<>();
        List<ParticipantDto.DeleteResponse> result;
        try {
            JoinCompetition join = joinCompetitionService.getJoinCompetition(joincompetitionDto.getUid(), joincompetitionDto.getCompetitionId()).get();
            result = joinCompetitionService.deleteJoinCompetition(joincompetitionDto);
            String formId = join.getFormId();
            if (formId != null) {
                competitionFormService.deleteForm(formId);
            }
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res); // 400
        }
        res.put("success", true);
        res.put("result", result);
        res.put("message", "대회 참가가 취소되었습니다.");

        return ResponseEntity.accepted().body(res); // 202
    }

    /**
     * 체육인 별 참가 대회 조회
     * @param userId : Member의 uid
     * @param page : 페이지 번호
     * @param size : 페이지 사이즈
     * @return : Slice<JoinCompetitionDto.UserJoinResponse>
     * @throws Exception
     */
    @GetMapping("/join/slice/{userId}")
    public ResponseEntity<Object> getJoinCompetitionSlice(@PathVariable Long userId,
                                                                      @RequestParam(required = false) Long page,
                                                                      @RequestParam(required = false) Long size) throws Exception {
        Map<String, Object> res = new HashMap<>();
        Slice<JoinCompetitionDto.UserJoinResponse> result = null;
        page = page == null ? 0 : page;
        size = size == null ? 15 : size;
        try {
            result =  joinCompetitionService.findJoinedCompetitionsByUid(userId, page, size);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
        res.put("success", true);
        res.put("result", result);

        return ResponseEntity.ok(res); // 200
    }
    @GetMapping("/all/{hostId}")
    public ResponseEntity<Object> getAllCompetitions(@PathVariable Long hostId,
                                                     @RequestParam(required = false) Long page,
                                                     @RequestParam(required = false) Long size
                                                     ) throws Exception {
        Map<String, Object> res = new HashMap<>();
        Slice<CompetitionDto.Summary> result = null;
        page = page == null ? 0 : page;
        size = size == null ? 15 : size;
        try {
            List<Competition> competitions = competitionService.getCompetitionSliceByHostId(hostId, page.intValue(), size.intValue()).getContent();
            result = new SliceImpl<>(competitions.stream().map(competition -> CompetitionDto.Summary.builder()
                    .competitionId(competition.getCompetitionId())
                    .name(competition.getName())
                    .host(MemberDto.Summary.builder()
                            .uid(competition.getHost().getUid())
                            .name(competition.getHost().getName())
                            .build())
                    .startDate(competition.getStartDate())
                    .posters(competition.getPosters())
                    .sportCategory(competition.getCategory())
                    .build()).toList());
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
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception) {
        Map<String, Object> res = new HashMap<>(){{
            put("success", false);
            put("message", exception.getMessage());
        }};
        return ResponseEntity.badRequest().body(res); // 400
    }

    @ExceptionHandler
    public ResponseEntity<Object> NullPoniterException(NullPointerException exception) {
        Map<String, Object> res = new HashMap<>(){{
            put("success", false);
            put("message", exception.getMessage());
        }};
        return ResponseEntity.badRequest().body(res); // 400
    }

}
