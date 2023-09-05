package PlayMakers.SportsIT.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Array;
import java.util.Arrays;
import java.util.Map;

@OpenAPIDefinition(
        info = @Info(title = "스포츠잇 API 명세서",
                description = "API 명세 및 데이터 스키마를 확인하고 API 테스트 호출 가능합니다.",
                version = "0.0.1"),
        servers = {
                @Server(url = "https://localhost:8443", description = "Local server"),
                @Server(url = "https://sports-it-test.store", description = "Test server")
        })
@Configuration
public class SwaggerConfig {

    // Schemas
    Schema PostResponse = new Schema<Map<String, Object>>()
            .addProperty("timestamp",new StringSchema().example("2023-08-30T13:17:04.846Z"))
            .addProperty("code",new StringSchema().example("201"))
            .addProperty("success",new StringSchema().example("true"))
            .addProperty("result",new StringSchema().example("{}"));


    Schema CompetitionForm = new Schema<Map<String, Object>> ()
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





    @Bean
    public GroupedOpenApi competitionOpenApi() {
        String[] paths = {"api/competitions/**"};

        return GroupedOpenApi.builder()
                .group("Competitions")
                .pathsToMatch(paths)
                .addOpenApiCustomizer(buildSecurityOpenApi())
                .addOpenApiCustomizer(registerSchemas())
                .build();
    }

    @Bean
    public GroupedOpenApi memberOpenApi() {
        String[] paths = {"api/member/**"};

        return GroupedOpenApi.builder()
                .group("Member")
                .pathsToMatch(paths)
                .build();
    }

    public OpenApiCustomizer buildSecurityOpenApi() {
        // 설정된 jwt 토큰을 Header에 넣어주는 코드
        return OpenApi -> OpenApi.addSecurityItem(new SecurityRequirement().addList("jwt token"))
                .getComponents().addSecuritySchemes("jwt token", new SecurityScheme()
                        .name("Authorization")
                        .type(SecurityScheme.Type.HTTP)
                        .in(SecurityScheme.In.HEADER)
                        .bearerFormat("JWT")
                        .scheme("bearer"));
    }

    public OpenApiCustomizer registerSchemas() {
        return openApi -> openApi.getComponents()
                .addSchemas("PostResponse", PostResponse)
                .addSchemas("CompetitionForm", CompetitionForm);
    }

}
