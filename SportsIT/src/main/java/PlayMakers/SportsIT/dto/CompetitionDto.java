package PlayMakers.SportsIT.dto;

import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.enums.CompetitionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor @NoArgsConstructor
@Data // @ToString, @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor
public class CompetitionDto {

    @Schema(description = "대회 ID", example = "1")
    private Long competitionId;
    @Schema(description = "대회 이름", example = "스포츠잇 팔씨름 대회")
    private String name;
    @Schema(description = "대회 주최자 객체")
    private Member host;
    @Schema(description = "대회 종목 코드 리스트 / 현재는 대회 생성시 필요 X", example = "{\"ARM_WRESTLING\"}")
    private List<String> categories;
    @Schema(description = "종목 코드", example = "ARM_WRESTLING")
    private SportCategory sportCategory;
    @Schema(description = "조회수", example = "30")
    @Builder.Default
    private Integer viewCount = 0;
    @Schema(description = "스크랩수", example = "10")
    @Builder.Default
    private Integer scrapCount = 0;
    @Schema(description = "모집 시작일", example = "2021-09-15T00:00:00")
    private LocalDateTime recruitingStart;
    @Schema(description = "모집 종료일", example = "2021-09-25T00:00:00")
    private LocalDateTime recruitingEnd;
    @Schema(description = "대회 시작일", example = "2023-09-30T00:00:00")
    private LocalDateTime startDate;
    @Schema(description = "대회 종료일", example = "2021-10-05T00:00:00")
    private LocalDateTime endDate;
    @Schema(description = "총 상금", example = "1000000")
    private Integer totalPrize;
    @Schema(description = "대회 상세 정보", example = "스포츠잇 팔씨름 대회를 개최합니다.")
    private String content;
    @Schema(description = "개최 주소1", example = "서울특별시 강남구")
    private String location;
    @Schema(description = "개최 주소2", example = "테헤란로 427")
    private String locationDetail;
    @Schema(description = "개최 주소 위도", example = "37.5665")
    private Double latitude;
    @Schema(description = "개최 주소 경도", example = "126.9780")
    private Double longitude;
    @Schema(description = "대회 상태 / 대회 생성시 필요 X", example = "RECRUITING")
    private CompetitionState state;
    @Schema(description = "대회 취소 사유 / 대회 생성시 필요 X", example = "우천으로 인해 불가피하게 대회를 취소하게 되었습니다.")
    private String stateDetail;
    @Schema(description = "대회 타입 / 대회 생성시 필요 X", example = "BASIC")
    private CompetitionType competitionType;
    @Schema(description = "대회 설문지 템플릿 ID / 대회 생성시 필요 X", example = "14")
    private String templateId;
    @Schema(description = "최대 참가자 수", example = "999")
    @Builder.Default
    private Integer maxPlayer = 999;
    @Schema(description = "최대 관람자 수", example = "999")
    @Builder.Default
    private Integer maxViewer = 999;
    @Schema(description = "대회 포스터 리스트")
    @Builder.Default
    private List<Poster> posters = new ArrayList<>();
    @Schema(description = "대회 규약 리스트")
    @Builder.Default
    private List<Agreement> agreements = new ArrayList<>();
    @Schema(description = "대회 게시물 생성일 / 대회 생성시 필요 X", example = "2021-09-15T00:00:00")
    private String createdDate;
    @Schema(description = "대회 게시물 수정일 / 대회 생성시 필요 X", example = "2021-09-15T00:00:00")
    private String updatedDate;
    private List<String> category;

    public Competition toEntity() {
        return Competition.builder()
                .competitionId(competitionId)
                .name(name)
                .host(host)
                .category(sportCategory)
                .viewCount(viewCount)
                .scrapCount(scrapCount)
                .startDate(startDate)
                .endDate(endDate)
                .recruitingStart(recruitingStart)
                .recruitingEnd(recruitingEnd)
                .totalPrize(totalPrize)
                .content(content)
                .location(location)
                .locationDetail(locationDetail)
                .latitude(latitude)
                .longitude(longitude)
                .state(state)
                .stateDetail(stateDetail)
                .competitionType(competitionType)
                .templateID(templateId)
                .maxPlayer(maxPlayer)
                .maxViewer(maxViewer)
                .posters(posters)
                .agreements(agreements)
                .build();
    }
    @Data
    @AllArgsConstructor @NoArgsConstructor
    @Builder
    public static class Summary {
        private Long competitionId;
        private String name;
        private MemberDto.Summary host;
        private SportCategory sportCategory;
        private List<Poster> posters;
        private LocalDateTime startDate;
    }

    @Builder
    @AllArgsConstructor @NoArgsConstructor
    @Data
    public static class Form {
        @Schema(description = "대회 이름", example = "스포츠잇 팔씨름 대회")
        @NonNull
        private String name;
        @Schema(description = "대회 종목 리스트", example = "[\"ARM_WRESTLING\"]")
        @NonNull
        private List<String> categories;
        @Schema(description = "대회 종목, deprecate 예정", example = "ARM_WRESTLING")
        @NonNull
        private SportCategory sportCategory;
        @Schema(description = "대회 모집 시작일", type = "string", format = "date-time", example = "2023-09-15T00:00:00Z")
        @NonNull
        private LocalDateTime recruitingStart;
        @Schema(description = "대회 모집 종료일", type = "string", format = "date-time", example = "2023-09-20T00:00:00Z")
        @NonNull
        private LocalDateTime recruitingEnd;
        @Schema(description = "대회 시작일", type = "string", format = "date-time", example = "2023-09-30T00:00:00Z")
        @NonNull
        private LocalDateTime startDate;
        @Schema(description = "대회 종료일", type = "string", format = "date-time", example = "2023-10-05T00:00:00Z")
        @NonNull
        private LocalDateTime endDate;
        @Schema(description = "총 상금", example = "1000000")
        private Integer totalPrize;
        @Schema(description = "대회 상세 정보", example = "스포츠잇 팔씨름 대회를 개최합니다.")
        @NonNull
        private String content;
        @Schema(description = "개최 주소1", example = "서울특별시 강남구 테헤란로 626")
        @NonNull
        private String location;
        @Schema(description = "개최 주소2", example = "메디톡스빌딩, 2층")
        @NonNull
        private String locationDetail;
        @Schema(description = "개최 주소 위도", example = "37.5665")
        @NonNull
        private Double latitude;
        @Schema(description = "개최 주소 경도", example = "126.9780")
        @NonNull
        private Double longitude;
        @Schema(description = "대회 신청서 양식 ID", example = "x8mn9Bwj8u4Sm5c69rUV")
        @NonNull
        private String templateId;
        @Schema(description = "최대 참가자 수, Null일 경우 999명", example = "300")
        private Integer maxPlayer;
        @Schema(description = "최대 관람자 수, Null일 경우 999명", example = "999")
        private Integer maxViewer;

        public CompetitionDto toAllArgsDto() {
            return CompetitionDto.builder()
                    .name(name)
                    .sportCategory(sportCategory)
                    .categories(categories)
                    .recruitingStart(recruitingStart)
                    .recruitingEnd(recruitingEnd)
                    .startDate(startDate)
                    .endDate(endDate)
                    .totalPrize(totalPrize)
                    .content(content)
                    .location(location)
                    .locationDetail(locationDetail)
                    .latitude(latitude)
                    .longitude(longitude)
                    .templateId(templateId)
                    .maxPlayer(maxPlayer)
                    .maxViewer(maxViewer)
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor @NoArgsConstructor
    @Data
    public static class Info {
        @Schema(description = "대회 ID", example = "1")
        private Long competitionId;
        @Schema(description = "대회 이름", example = "스포츠잇 팔씨름 대회")
        private String name;
        @Schema(description = "대회 주최자 객체")
        private MemberDto.Summary host;
        @Schema(description = "대회 종목 코드 리스트 / 현재는 대회 생성시 필요 X", example = "{\"ARM_WRESTLING\"}")
        private Set<Category> categories;
        @Schema(description = "종목 코드", example = "ARM_WRESTLING")
        private SportCategory sportCategory;
        @Schema(description = "조회수", example = "30")
        @Builder.Default
        private Integer viewCount = 0;
        @Schema(description = "스크랩수", example = "10")
        @Builder.Default
        private Integer scrapCount = 0;
        @Schema(description = "모집 시작일", example = "2021-09-15T00:00:00")
        private LocalDateTime recruitingStart;
        @Schema(description = "모집 종료일", example = "2021-09-25T00:00:00")
        private LocalDateTime recruitingEnd;
        @Schema(description = "대회 시작일", example = "2023-09-30T00:00:00")
        private LocalDateTime startDate;
        @Schema(description = "대회 종료일", example = "2021-10-05T00:00:00")
        private LocalDateTime endDate;
        @Schema(description = "총 상금", example = "1000000")
        private Integer totalPrize;
        @Schema(description = "대회 상세 정보", example = "스포츠잇 팔씨름 대회를 개최합니다.")
        private String content;
        @Schema(description = "개최 주소1", example = "서울특별시 강남구")
        private String location;
        @Schema(description = "개최 주소2", example = "테헤란로 427")
        private String locationDetail;
        @Schema(description = "개최 주소 위도", example = "37.5665")
        private Double latitude;
        @Schema(description = "개최 주소 경도", example = "126.9780")
        private Double longitude;
        @Schema(description = "대회 상태 / 대회 생성시 필요 X", example = "RECRUITING")
        private CompetitionState state;
        @Schema(description = "대회 타입 / 대회 생성시 필요 X", example = "BASIC")
        private CompetitionType competitionType;
        @Schema(description = "대회 설문지 템플릿 ID / 대회 생성시 필요 X", example = "14")
        private String templateId;
        @Schema(description = "최대 참가자 수", example = "999")
        @Builder.Default
        private Integer maxPlayer = 999;
        @Schema(description = "최대 관람자 수", example = "999")
        @Builder.Default
        private Integer maxViewer = 999;
        @Schema(description = "대회 포스터 리스트")
        @Builder.Default
        private List<Poster> posters = new ArrayList<>();
        @Schema(description = "대회 규약 리스트")
        @Builder.Default
        private List<Agreement> agreements = new ArrayList<>();
        @Schema(description = "참가 여부")
        @Builder.Default
        private boolean joined = false;
        @Schema(description = "스크랩 여부")
        @Builder.Default
        private boolean scrapped = false;

        public static Info entityToInfo(Competition competition) {
            Member host = competition.getHost();
            return Info.builder()
                    .competitionId(competition.getCompetitionId())
                    .name(competition.getName())
                    .host(new MemberDto.Summary(host.getUid(), host.getName()))
                    .categories(competition.getCategories())
                    .sportCategory(competition.getCategory())
                    .viewCount(competition.getViewCount())
                    .scrapCount(competition.getScrapCount())
                    .recruitingStart(competition.getRecruitingStart())
                    .recruitingEnd(competition.getRecruitingEnd())
                    .startDate(competition.getStartDate())
                    .endDate(competition.getEndDate())
                    .totalPrize(competition.getTotalPrize())
                    .content(competition.getContent())
                    .location(competition.getLocation())
                    .locationDetail(competition.getLocationDetail())
                    .latitude(competition.getLatitude())
                    .longitude(competition.getLongitude())
                    .state(competition.getState())
                    .competitionType(competition.getCompetitionType())
                    .templateId(competition.getTemplateID())
                    .maxPlayer(competition.getMaxPlayer())
                    .maxViewer(competition.getMaxViewer())
                    .posters(competition.getPosters())
                    .agreements(competition.getAgreements())
                    .build();
        }
    }
}
