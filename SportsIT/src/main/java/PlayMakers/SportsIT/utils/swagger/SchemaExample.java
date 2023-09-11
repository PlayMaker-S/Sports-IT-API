package PlayMakers.SportsIT.utils.swagger;

import PlayMakers.SportsIT.domain.Member;
import io.swagger.v3.oas.models.media.*;

import java.util.Map;

public class SchemaExample {
    public static Schema PostResponse = new Schema<Map<String, Object>>()
            .addProperty("timestamp",new StringSchema().example("2023-08-30T13:17:04.846Z"))
            .addProperty("code",new StringSchema().example("201"))
            .addProperty("success",new StringSchema().example("true"))
            .addProperty("result",new StringSchema().example("{}"));

    public static Schema Competition = new Schema<Map<String, Object>>()
            .addProperty("createdDate", new StringSchema().example("2023-08-30T13:17:04.846Z"))
            .addProperty("updateDate", new StringSchema().example("2023-08-30T13:17:04.846Z"))
            .addProperty("competitionId", new IntegerSchema().example(2791))
            .addProperty("name", new StringSchema().example("스포츠잇 팔씨름 대회"))
            .addProperty("viewCount", new IntegerSchema().example(0))
            .addProperty("scrapCount", new IntegerSchema().example(0))
            .addProperty("recruitingStart", new StringSchema().example("2023-09-15T00:00:00Z"))
            .addProperty("recruitingEnd", new StringSchema().example("2023-09-20T00:00:00Z"))
            .addProperty("startDate", new StringSchema().example("2023-09-25T00:00:00Z"))
            .addProperty("endDate", new StringSchema().example("2023-09-30T00:00:00Z"))
            .addProperty("totalPrize", new IntegerSchema().example(1000000))
            .addProperty("content", new StringSchema().example("스포츠잇 팔씨름 대회"))
            .addProperty("location", new StringSchema().example("서울특별시 강남구 테헤란로 427"))
            .addProperty("locationDetail", new StringSchema().example("지하 1층 경기장"))
            .addProperty("latitude", new NumberSchema().example(37.503))
            .addProperty("longitude", new NumberSchema().example(127.044))
            .addProperty("state", new StringSchema().example("RECRUITING"))
            .addProperty("stateDetail", new StringSchema().example(""))
            .addProperty("competitionType", new StringSchema().example("FREE"))
            .addProperty("categories", new ArraySchema().items(new JsonSchema()
                    .addProperty("category", new StringSchema().example("ARM_WRESTLING"))
                    .addProperty("name", new StringSchema().example("팔씨름"))))
            .addProperty("templateID", new StringSchema().example("x8mn9Bwj8u4Sm5c69rUV"))
            .addProperty("maxPlayer", new IntegerSchema().example(300))
            .addProperty("maxViewer", new IntegerSchema().example(999))
            .addProperty("posters", new ArraySchema().items(new JsonSchema()
                    //.addProperty("posterId", new IntegerSchema().example(1))
                    .addProperty("posterUrl", new StringSchema().example("https://sportsit.s3.ap-northeast-2.amazonaws.com/competition/1/1.jpg"))))
            .addProperty("agreements", new ArraySchema().items(new JsonSchema()
                    //.addProperty("agreementId", new IntegerSchema().example(1))
                    .addProperty("name", new StringSchema().example("스포츠잇 대회 참가 동의서"))
                    .addProperty("agreementUrl", new StringSchema().example("https://sportsit.s3.ap-northeast-2.amazonaws.com/competition/1/1.jpg"))))
            .addProperty("host", new JsonSchema()
                    .addProperty("uid", new IntegerSchema().example(1))
                    .addProperty("name", new StringSchema().example("홍길동"))
                    .addProperty("profileImageUrl", new StringSchema().example("https://sportsit.s3.ap-northeast-2.amazonaws.com/competition/1/1.jpg")));
    //public static Schema Competition = new Schema
    public static Schema CompetitionForm = new Schema<Map<String, Object>> ()
            .addProperty("timestamp",new StringSchema().example("2023-08-30T13:17:04.846Z"))
            .addProperty("code",new StringSchema().example("200"))
            .addProperty("success",new StringSchema().example("true"))
            .addProperty("result",new JsonSchema()
                    .addProperty("name", new StringSchema().example("스포츠잇 팔씨름 대회"))
                    .addProperty("categories", new ArraySchema().items(new StringSchema().example("ARM_WRESTLING")))
                    .addProperty("sportCategory", new StringSchema().example("ARM_WRESTLING"))
                    .addProperty("recruitingStart", new StringSchema().example("2023-09-15T00:00:00Z"))
                    .addProperty("recruitingEnd", new StringSchema().example("2023-09-20T00:00:00Z"))
                    .addProperty("startDate", new StringSchema().example("2023-09-30T00:00:00Z"))
                    .addProperty("endDate", new StringSchema().example("2023-10-05T00:00:00Z"))
                    .addProperty("totalPrize", new IntegerSchema().example(1000000))
                    .addProperty("content", new StringSchema().example("스포츠잇 팔씨름 대회를 개최합니다."))
                    .addProperty("location", new StringSchema().example("서울특별시 강남구 테헤란로 626"))
                    .addProperty("locationDetail", new StringSchema().example("메디톡스빌딩, 2층"))
                    .addProperty("latitude", new NumberSchema().example(37.5665))
                    .addProperty("longitude", new NumberSchema().example(126.978))
                    .addProperty("templateId", new StringSchema().example("x8mn9Bwj8u4Sm5c69rUV"))
                    .addProperty("maxPlayer", new IntegerSchema().example(300))
                    .addProperty("maxViewer", new IntegerSchema().example(999)));

    public static Schema JoinCount = new Schema<Map<String, Object>>()
            .addProperty("timestamp",new StringSchema().example("2023-08-30T13:17:04.846Z"))
            .addProperty("code",new StringSchema().example("200"))
            .addProperty("success",new StringSchema().example("true"))
            .addProperty("result",new JsonSchema()
            .addProperty("availablePlayer", new StringSchema().example("300"))
            .addProperty("availableViewer", new StringSchema().example("999")));

    public static Schema CompetitionSlice = new Schema<Map<String, Object>>()
            .addProperty("timestamp",new StringSchema().example("2023-08-30T13:17:04.846Z"))
            .addProperty("code",new StringSchema().example("200"))
            .addProperty("success",new StringSchema().example("true"))
            .addProperty("result",new JsonSchema()
                    .addProperty("content", new ArraySchema().items(Competition))
                    .addProperty("size", new IntegerSchema().example(10))
                    .addProperty("number", new IntegerSchema().example(0))
                    .addProperty("first", new BooleanSchema().example(true))
                    .addProperty("last", new BooleanSchema().example(true))
                    .addProperty("numberOfElements", new IntegerSchema().example(1))
                    .addProperty("empty", new BooleanSchema().example(false)));

}
