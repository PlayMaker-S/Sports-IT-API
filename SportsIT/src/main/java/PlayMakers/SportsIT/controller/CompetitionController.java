package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.dto.*;
import PlayMakers.SportsIT.exceptions.ErrorCode;
import PlayMakers.SportsIT.exceptions.RequestDeniedException;
import PlayMakers.SportsIT.exceptions.UnAuthorizedException;
import PlayMakers.SportsIT.service.*;
import PlayMakers.SportsIT.utils.api.ApiUtils;
import PlayMakers.SportsIT.utils.api.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
            host = memberService.getMember(user);
        } catch (BadCredentialsException e) {
            log.error("로그인 에러");
            throw new PlayMakers.SportsIT.exceptions.EntityNotFoundException(ErrorCode.EMPTY_TOKEN, "Access Token이 비어있거나 잘못 되었습니다.");
        } catch (EntityNotFoundException e) {
            log.error("User Not Found : {}", user.getUsername());
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
     * @param keyword String
     * @param filteringConditions List<String>
     * @param orderBy String
     * @param page String
     * @param size String
     * @return ResponseEntity<?> - 200 : success: true, result: Slice<Competition>
     */
    @Operation(summary = "대회 슬라이스 조회 API", description = """
            \uD83D\uDCCC 대회 목록을 불러옵니다. Pagination으로 구현되어 page 값이 필요합니다.
            \n"keyword"는 검색시 대회의 {대회 제목, 주최자 이름, 종목 한글명}에 포함되어있을 경우 해당 대회를 검색 결과에 포함시킵니다. 전체 조회를 원할 경우 비워둡니다.
            \n"orderBy"는 대회 목록 정렬시 사용합니다. 기본 값은 createdDate입니다.
            \n"filteryBy"는 대회 목록을 필터링 할 경우 사용합니다. 대회 상태(PLANNING~END)는 OR 연산을, 일부 조건(recruitignEnd, totalPrize, recommend)는 AND 연산을 수행한 결과를 반환합니다. 복수 개의 필터링 사용이 가능합니다. (ex. filteringConditions=RECRUITING&filteringConditions=PLANNING)
            \n"page"와 "size"는 Pagination 파라미터입니다. 매 요청마다 size는 고정시키고, page만 변경하여 새로운 대회 데이터를 받아올 수 있습니다.\n\n
            ✔️ 성공시 result와 success: true를 반환합니다. result에는 대회 목록(content)와 page 정보(size, number, first, last, numberOfElements, empty)를 확인할 수 있습니다. (200)\n\n
            ❌ 실패시 HTTP Status Code와 에러 코드를 반환합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대회 슬라이스 조회 성공", content = @Content(schema = @Schema(ref = "#/components/schemas/CompetitionSlice"))),
    })
    @GetMapping("/slice")
    public ResponseEntity<CommonResponse<Slice<Competition>>> getCompetitionSlice(
            @Parameter(name="keyword", description="검색어(없으면 전체조회)", examples= {@ExampleObject(name="전체 조회"), @ExampleObject(name="검색", value="팔씨름")}) @RequestParam(required = false) String keyword,
            @Parameter(name="filterBy", description="필터 조건", examples={
                    @ExampleObject(name="PLANNING", value="PLANNING", description="모집 전 : 대회 모집일 전"),
                    @ExampleObject(name="RECRUITING", value="RECRUITING", description="모집 중 : 대회 모집 시작 ~ 모집 종료 전"),
                    @ExampleObject(name="RECRUITING_END", value="RECRUITING_END", description="시작 전 : 대회 모집 종료 ~ 대회 시작 전"),
                    @ExampleObject(name="IN_PROGRESS", value="IN_PROGRESS", description="대회 중 : 대회 시작 ~ 대회 종료 전"),
                    @ExampleObject(name="END", value="END", description="대회 종료 : 대회 종료 후"),
                    @ExampleObject(name="recruitingEnd", value="recruitingEnd", description="모집 마감 임박 : 대회 모집 종료 7일 이내"),
                    @ExampleObject(name="totalPrize", value="totalPrize", description="큰 상금 : 대회 상금 10만원 이상"),
                    @ExampleObject(name="recommend", value="recommend", description="추천 대회 : 프리미엄 대회"),
            }) @RequestParam(value = "filterBy", required = false) List<String> filteringConditions,
            @Parameter(name="orderBy", description="정렬 조건", examples={
                    @ExampleObject(name="등록순", value="createdDate"),
                    @ExampleObject(name="조회수 순", value="viewCount"),
            }) @RequestParam(required = false) String orderBy,
            @Parameter(name="page", description="페이지 번호", examples= {@ExampleObject(name="0", value="0")})
            @RequestParam String page,
            @Parameter(name="size", description="한 페이지당 대회 수", examples= {@ExampleObject(name="10", value="10")})
            @RequestParam String size) {
        log.info("대회 slice 요청: {} {} {} {} {}", keyword, filteringConditions, orderBy, page, size);

        Slice<Competition> competitions = competitionService.getCompetitionSlice(keyword, filteringConditions, orderBy, parseInt(page), parseInt(size));
        return ResponseEntity.ok(ApiUtils.success(HttpStatus.OK.value(), competitions)); // 200
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
        boolean joined = (user != null) && joinCompetitionService.checkAlreadyJoined(Long.parseLong(user.getUsername()), competitionId);

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
            ✔️ 성공시 success: true를 반환합니다. (204)\n\n
            ❌ 실패시 HTTP Status Code와 에러 코드를 반환합니다.
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
            @Parameter(name = "competitionId", description = "대회 ID", required = true, in = ParameterIn.PATH, example="2790")
            @PathVariable Long competitionId,
            @Parameter(name = "competitionDto", description = "대회 입력 객체", required = true)
            @RequestBody CompetitionDto.Form dto,
            @AuthenticationPrincipal User user) throws Exception {

        Competition competition = competitionService.findById(competitionId);
        Member host = memberService.getMember(user);
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

    @Operation(summary = "대회 삭제 API", description = """
            \uD83D\uDCCC 대회 ID로 대회를 취소합니다. (대회 삭제시 JoinCompetition 테스트 진행해야 됩니다.)\n\n
            ✔️ 성공시 success: true를 반환합니다. (204)\n\n
            ❌ 실패시 HTTP Status Code와 에러 코드를 반환합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "대회 삭제 성공", content = @Content(schema = @Schema(ref = "#/components/schemas/PostResponse"))),
            @ApiResponse(responseCode = "401", description = "(AUTH-001) Token이 비어있는 경우", content = @Content),
            @ApiResponse(responseCode = "403", description = "(COMMON-004) 로그인한 사용자가 관리자나 해당 대회를 게시한 사용자가 아닐 경우\n\n" +
                    "(AUTH-004) 대회가 이미 시작되었거나 종료된 경우", content = @Content),
            @ApiResponse(responseCode = "404", description = "(COMPETITION-003) 해당 ID의 대회가 존재하지 않을 경우", content = @Content)
    })
    @DeleteMapping("/{competitionId}")
    public ResponseEntity<CommonResponse<Object>> deleteCompetition(
            @Parameter(name = "competitionId", description = "대회 ID", required = true, in = ParameterIn.PATH, example="2790")
            @PathVariable Long competitionId,
            @AuthenticationPrincipal User user) throws Exception {

        log.info("대회 삭제 요청: {}", competitionId);

        Competition competition = competitionService.findById(competitionId);

        Member host = memberService.getMember(user);

        if (!competition.getHost().getUid().equals(host.getUid()) && host.getMemberType().stream().noneMatch(
                memberType -> memberType.getRoleName().equals("ROLE_ADMIN"))) {
            throw new UnAuthorizedException(ErrorCode.ACCESS_DENIED, "관리자 또는 작성자 본인만 대회를 삭제할 수 있습니다.");
        }

        competitionService.checkCompetitionNotStarted(competition);

        competitionService.delete(competitionId);

        return ResponseEntity.ok(ApiUtils.success(HttpStatus.NO_CONTENT.value(), null)); // 204
    }

    /**
     * 대회 참가시 참가 가능한 인원 수를 반환, 대회 참가가 불가능할 경우 예외 발생
     *
     * @param competitionId Long
     * @param user          AuthenticationPrincipal User
     * @return ResponseEntity<Object>
     * @throws Exception 대회 참가가 불가능할 경우 예외 발생
     */
    @Operation(summary = "대회 여석 조회 API", description = """
            \uD83D\uDCCC 대회 ID로 현재 참가 가능한 선수 및 참관자 수를 반환합니다. \n\n
            ✔️ 성공시 success: true와 availablePlayer, availableViewr를 result에 담아 반환합니다. (200)\n\n
            ❌ 실패시 HTTP Status Code와 에러 코드를 반환합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대회 참가 여석 조회 성공", content = @Content(schema = @Schema(ref = "#/components/schemas/JoinCount"))),
            @ApiResponse(responseCode = "400", description = "(COMPETITION-005) 대회 모집 기간이 아니거나 이미 참가한 대회일 경우", content = @Content),
            @ApiResponse(responseCode = "401", description = "(AUTH-001) Token이 비어있는 경우", content = @Content),
            @ApiResponse(responseCode = "403", description = "(AUTH-005) 로그인한 사용자가 체육인 권한이 없을 경우", content = @Content),
            @ApiResponse(responseCode = "404", description = "(COMPETITION-003) 해당 ID의 대회가 존재하지 않을 경우", content = @Content)
    })
    @GetMapping("/{competitionId}/join/init")
    public CommonResponse<Map<String, String>> initJoin(
            @Parameter(name = "competitionId", description = "대회 ID", required = true, in = ParameterIn.PATH, example="2790")
            @PathVariable Long competitionId,
            @AuthenticationPrincipal User user) throws Exception{
        Member member = memberService.getMember(user);

        try {
            Map<String, String> result = joinCompetitionService.getJoinCounts(competitionId, member);

            return ApiUtils.success(HttpStatus.OK.value(), result);
        } catch (PlayMakers.SportsIT.exceptions.EntityNotFoundException e) {
            throw new PlayMakers.SportsIT.exceptions.EntityNotFoundException(ErrorCode.COMPETITION_NOT_FOUND, e.getMessage()+competitionId);
        } catch(IllegalAccessException e) {
            throw new RequestDeniedException(ErrorCode.COMPETITION_NOT_AVAILABLE, e.getMessage());
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
    @Operation(summary = "대회 참가 양식 조회 API", description = """
            \uD83D\uDCCC 대회 ID와 참가 타입으로 대회 참가 양식을 반환합니다. \n
            ✔️ 성공시 success: true와 agreements, template을 result에 담아 반환합니다. (200) \n
            ❌ 실패시 HTTP Status Code와 에러 코드를 반환합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대회 참가 양식 조회 성공", content = @Content(schema = @Schema(ref = "#/components/schemas/JoinFormat"))),
            @ApiResponse(responseCode = "400", description = "(COMPETITION-005) 대회 모집 기간이 아니거나 이미 참가한 대회일 경우", content = @Content),
            @ApiResponse(responseCode = "401", description = "(AUTH-001) Token이 비어있는 경우", content = @Content),
            @ApiResponse(responseCode = "403", description = "(AUTH-005) 로그인한 사용자가 체육인 권한이 없을 경우", content = @Content),
            @ApiResponse(responseCode = "404", description = "(COMPETITION-003) 해당 ID의 대회가 존재하지 않을 경우", content = @Content)
    })
    @GetMapping("/{competitionId}/join/format")
    public ResponseEntity<Object> getCompetitionContents(
            @Parameter(name = "competitionId", description = "대회 ID", required = true, in = ParameterIn.PATH, example="2790") @PathVariable Long competitionId,
            @Parameter(name = "joinType", description = "참가 타입", required = true, in = ParameterIn.QUERY, examples= {
                    @ExampleObject(name = "선수", description = "선수로 참가할 경우", value="player"),
                    @ExampleObject(name = "관람", description = "관람객으로 참가할 경우", value="viewer")})
            @RequestParam String joinType,
            @AuthenticationPrincipal User user) throws Exception{
        Member member = memberService.getMember(user);

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
     * @param competitionId Long
     * @param joinType String
     * @param formDto CompetitionFormDto
     * @return
     * @throws Exception
     */
    @Operation(summary = "대회 참가 신청서 제출 API", description = """
            \uD83D\uDCCC 선수 대회 참가 신청서를 제출하기 위한 API 입니다. 자세한 내용은 노션의 대회 참가 API를 참고해주세요. (https://www.notion.so/API-43945089ce72418588d85879e380c4ec) \n
            ✔️ 성공시 success: true와 agreements, template을 result에 담아 반환합니다. (200) \n
            ❌ 실패시 HTTP Status Code와 에러 코드를 반환합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대회 참가 신청서 성공", content = @Content(schema = @Schema(ref = "#/components/schemas/JoinFormSubmitResponse"))),
            @ApiResponse(responseCode = "400", description = "(COMPETITION-005) 대회 모집 기간이 아니거나 이미 참가한 대회일 경우", content = @Content),
            @ApiResponse(responseCode = "401", description = "(AUTH-001) Token이 비어있는 경우", content = @Content),
            @ApiResponse(responseCode = "403", description = "(AUTH-005) 로그인한 사용자가 체육인 권한이 없을 경우", content = @Content),
            @ApiResponse(responseCode = "404", description = "(COMPETITION-003) 해당 ID의 대회가 존재하지 않을 경우", content = @Content)
    })
    @PostMapping("/{competitionId}/join/format")
    public ResponseEntity<Object> postJoinForm(
            @Parameter(name = "competitionId", description = "대회 ID", required = true, in = ParameterIn.PATH, example="2790") @PathVariable Long competitionId,
            @Parameter(name = "joinType", description = "참가 타입", required = true, in = ParameterIn.QUERY, examples= {
                    @ExampleObject(name = "선수", description = "선수로 참가할 경우", value="player"),
                    @ExampleObject(name = "관람", description = "관람객으로 참가할 경우", value="viewer")})
            @RequestParam String joinType,
            @Parameter(name = "formDto", description = "대회 참가 신청서", required = true, schema = @Schema(implementation = CompetitionFormDto.class))
            @RequestBody CompetitionFormDto formDto,
            @AuthenticationPrincipal User user) throws Exception{

        Member member = memberService.getMember(user);
        Competition target = competitionService.findById(competitionId);
        Map<String, Object> res = new HashMap<>();

        if (joinCompetitionService.checkAlreadyJoined(member.getUid(), competitionId)) {
            throw new RequestDeniedException(ErrorCode.COMPETITION_NOT_AVAILABLE, "이미 참가한 대회입니다.");
        }
        JoinCompetition.joinType type = getJoinType(joinType);

        joinCompetitionService.checkJoinable(competitionId, type);

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
        Member member = memberService.getMember(user);

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
        Member member = memberService.getMember(user);
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

    @Operation(summary = "대회 신청 템플릿 등록 API", description = """
            \uD83D\uDCCC 대회 신청(선수) 템플릿을 등록합니다.\n\n
            ✔️ 성공시 success: true와 result에 생성된 템플릿 ID를 반환합니다. (201)\n\n
            ❌ 실패시 HTTP Status Code와 에러 코드를 반환합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "대회 템플릿(선수) 등록 성공", content = @Content(schema = @Schema(ref = "#/components/schemas/TemplateIdResponse"))),
    })
    @PostMapping("/template")
    public ResponseEntity<CommonResponse> createTemplate(@RequestBody CompetitionTemplate template) throws Exception {
        log.info("템플릿 생성 요청 Controller: {}", template);
        String docId = competitionTemplateService.saveTemplate(template);

        return ResponseEntity.created(URI.create("/api/competitions/template/" + docId)) // Location Header에 생성된 리소스의 URI를 담아서 보냄
                .body(ApiUtils.created(CREATED.value(), docId)); // 201
    }

    @Operation(summary = "대회 신청 템플릿 수정 API", description = """
            \uD83D\uDCCC 대회 신청(선수) 템플릿을 수정합니다.\n\n
            ✔️ 성공시 success: true를 반환합니다. (204)\n\n
            ❌ 실패시 HTTP Status Code와 에러 코드를 반환합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "대회 템플릿(선수) 수정 성공", content = @Content(schema = @Schema(ref = "#/components/schemas/PostResponse"))),
    })
    @PutMapping("/template/{templateId}")
    public ResponseEntity<CommonResponse> updateTemplate(@PathVariable String templateId, @RequestBody CompetitionTemplate template) throws Exception {
        log.info("템플릿 수정 요청 Controller: {}", template);
        competitionTemplateService.updateTemplate(templateId, template);

        return ResponseEntity.ok(ApiUtils.success(HttpStatus.NO_CONTENT.value(), null)); // 204
    }

    @Operation(summary = "대회 신청 템플릿 조회 API", description = """
            \uD83D\uDCCC 대회 신청(선수) 템플릿을 등록합니다.\n\n
            ✔️ 성공시 success: true와 result에 생성된 템플릿 ID를 반환합니다. (200)\n\n
            ❌ 실패시 HTTP Status Code와 에러 코드를 반환합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "대회 템플릿(선수) 등록 성공", content = @Content(schema = @Schema(ref = "#/components/schemas/TemplateGetResponse"))),
            @ApiResponse(responseCode = "404", description = "요청한 템플릿 ID를 찾을 수 없을 경우", content = @Content),
    })
    @GetMapping("/template/{templateId}")
    public ResponseEntity<?> getTemplate(@PathVariable String templateId) throws Exception {
        log.info("템플릿 조회 요청 Controller: {}", templateId);
        CompetitionTemplate template = competitionTemplateService.getTemplate(templateId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("result", template);
        return ResponseEntity.ok(ApiUtils.success(HttpStatus.OK.value(), template)); // 200
    }

    @Operation(summary = "대회 신청 템플릿 삭제 API", description = """
            \uD83D\uDCCC 대회 신청(선수) 템플릿을 삭제합니다.\n\n
            ✔️ 성공시 success: true를 반환합니다. (204)\n\n
            ❌ 실패시 HTTP Status Code와 에러 코드를 반환합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "대회 템플릿(선수) 삭제 성공", content = @Content(schema = @Schema(ref = "#/components/schemas/PostResponse"))),
            @ApiResponse(responseCode = "404", description = "요청한 템플릿 ID를 찾을 수 없을 경우", content = @Content),
    })
    @DeleteMapping("/template/{templateId}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String templateId) throws Exception {
        log.info("템플릿 삭제 요청 Controller: {}", templateId);
        competitionTemplateService.deleteTemplate(templateId);
        return ResponseEntity.noContent().build(); // 204
    }

    @Operation(summary = "대회 신청서(선수) 조회 API", description = """
            \uD83D\uDCCC 대회 신청(선수) 템플릿 목록을 조회합니다.\n\n
            ✔️ 성공시 success: true와 result에 템플릿 목록을 반환합니다. (200)\n\n
            ❌ 실패시 HTTP Status Code와 에러 코드를 반환합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대회 신청서(선수) 조회 성공", content = @Content(schema = @Schema(ref = "#/components/schemas/JoinCount"))),
            @ApiResponse(responseCode = "404", description = "요청한 신청서 ID를 찾을 수 없을 경우", content = @Content),
    })
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
    public ResponseEntity<Object> NullPointerException(NullPointerException exception) {
        Map<String, Object> res = new HashMap<>(){{
            put("success", false);
            put("message", exception.getMessage());
        }};
        return ResponseEntity.badRequest().body(res); // 400
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



}
