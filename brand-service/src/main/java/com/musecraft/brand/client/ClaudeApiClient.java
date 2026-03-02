package com.musecraft.brand.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Claude API 클라이언트
 * TODO: Claude API 연동 시 아래 주석을 해제하고 MockBrandIdentityResult 부분을 제거
 *
 * 연동 방법:
 *  1. .env 파일에 CLAUDE_API_KEY=sk-ant-api03-... 추가
 *  2. brand-service/build.gradle에 webflux 의존성 추가
 *  3. generateBrandIdentity() 메서드 내 실제 API 호출 코드 활성화
 */
@Slf4j
@Component
public class ClaudeApiClient {

//    private static final String CLAUDE_API_URL = "https://api.anthropic.com";
//    private static final String MODEL = "claude-sonnet-4-6";
//
//    private final WebClient.Builder webClientBuilder;
//    private final ObjectMapper objectMapper;
//
//    @Value("${claude.api.key}")
//    private String apiKey;

    /**
     * 브랜드명 + 키워드를 기반으로 브랜드 아이덴티티 생성
     * 현재: Mock 데이터 반환 (Claude API 미연동)
     */
    public BrandIdentityResult generateBrandIdentity(String brandName, String keywords) {
        log.info("브랜드 아이덴티티 생성 요청 - brandName: {}, keywords: {}", brandName, keywords);

        // TODO: Claude API 연동 시 아래 Mock 코드 제거 후 실제 API 호출 코드로 교체
        return mockGenerate(brandName, keywords);

//        --- 실제 Claude API 호출 코드 (연동 시 활성화) ---
//        String prompt = buildBrandIdentityPrompt(brandName, keywords);
//        Map<String, Object> requestBody = Map.of(
//                "model", MODEL,
//                "max_tokens", 1024,
//                "messages", List.of(Map.of("role", "user", "content", prompt))
//        );
//        WebClient client = webClientBuilder
//                .baseUrl(CLAUDE_API_URL)
//                .defaultHeader("x-api-key", apiKey)
//                .defaultHeader("anthropic-version", "2023-06-01")
//                .defaultHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
//                .build();
//        try {
//            String responseBody = client.post().uri("/v1/messages")
//                    .bodyValue(requestBody).retrieve().bodyToMono(String.class).block();
//            return parseResponse(responseBody);
//        } catch (Exception e) {
//            log.error("Claude API 호출 실패: {}", e.getMessage());
//            throw new RuntimeException("Claude API 호출에 실패했습니다.", e);
//        }
    }

    /**
     * Mock 브랜드 아이덴티티 생성 (Claude API 미연동 시 사용)
     * 브랜드명과 키워드를 조합해 간단한 결과를 반환
     */
    private BrandIdentityResult mockGenerate(String brandName, String keywords) {
        String slogan = brandName + "와 함께하는 특별한 순간";
        String brandStory = brandName + "은 " + keywords + "을 핵심 가치로 삼아, "
                + "사람들의 일상에 영감을 전하는 브랜드입니다. "
                + "우리는 콘텐츠를 통해 더 나은 삶의 방식을 제안합니다.";
        Map<String, String> colorPalette = Map.of(
                "primary", "#7c3aed",
                "secondary", "#06b6d4",
                "accent", "#f97316"
        );
        log.info("Mock 브랜드 아이덴티티 생성 완료 - brandName: {}", brandName);
        return new BrandIdentityResult(slogan, brandStory, colorPalette, 0, 0);
    }

//    private String buildBrandIdentityPrompt(String brandName, String keywords) { ... }
//    private BrandIdentityResult parseResponse(String responseBody) throws Exception { ... }

    public record BrandIdentityResult(
            String slogan,
            String brandStory,
            Map<String, String> colorPalette,
            int inputTokens,
            int outputTokens
    ) {}
}
