package PlayMakers.SportsIT.utils.swagger;

import io.swagger.v3.oas.models.media.*;

import java.util.Map;

public class SchemaExample {
    public static Schema PostResponse = new Schema<Map<String, Object>>()
            .addProperty("timestamp",new StringSchema().example("2023-08-30T13:17:04.846Z"))
            .addProperty("code",new StringSchema().example("201"))
            .addProperty("success",new StringSchema().example("true"))
            .addProperty("result",new StringSchema().example("{}"));


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
}
