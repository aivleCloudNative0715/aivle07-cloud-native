package aivlecloudnative.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import lombok.Data;
import org.slf4j.Logger; // SLF4J Logger 임포트
import org.slf4j.LoggerFactory; // SLF4J LoggerFactory 임포트

@Service
public class AIServiceSystem {

    private static final Logger log = LoggerFactory.getLogger(AIServiceSystem.class); // SLF4J Logger 사용

    private final WebClient gptWebClient;
    private final WebClient imageWebClient;
    private final String gptModel;
    private final String imageModel;
    private final String openAiApiKey; // 사용되지 않지만 주입은 유지
    private final ObjectMapper objectMapper;

    public AIServiceSystem(
            WebClient.Builder webClientBuilder,
            @Value("${spring.gpt.base-url}") String gptBaseUrl,
            @Value("${spring.gpt.image-url}") String imageBaseUrl,
            @Value("${spring.gpt.model}") String gptModel,
            @Value("${spring.gpt.image-model}") String imageModel,
            @Value("${spring.gpt.api-key}") String openAiApiKey) {

        this.openAiApiKey = openAiApiKey;
        this.gptModel = gptModel;
        this.imageModel = imageModel;
        this.objectMapper = new ObjectMapper();

        this.gptWebClient = webClientBuilder.baseUrl(gptBaseUrl)
                .defaultHeader("Authorization", "Bearer " + openAiApiKey)
                .build();

        this.imageWebClient = webClientBuilder.baseUrl(imageBaseUrl)
                .defaultHeader("Authorization", "Bearer " + openAiApiKey)
                .build();
    }

    @Data
    public static class AIResponse {
        private String coverImageUrl;
        private String ebookUrl;
        private String category;
        private Integer price;
    }

    @Data
    private static class ChatCompletionRequest {
        private String model;
        private List<Message> messages;
        private double temperature = 0.7;

        public ChatCompletionRequest(String model, List<Message> messages) {
            this.model = model;
            this.messages = messages;
        }
    }

    @Data
    private static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    @Data
    private static class ChatCompletionResponse {
        private List<Choice> choices;
    }

    @Data
    private static class Choice {
        private Message message;
    }

    @Data
    private static class ImageGenerationRequest {
        private String model;
        private String prompt;
        private int n = 1;
        private String size = "1024x1024";
        private String quality = "standard";
        private String style = "vivid";
        @JsonProperty("response_format")
        private String responseFormat = "url";
    }

    @Data
    private static class ImageGenerationResponse {
        private long created;
        private List<ImageData> data;
    }

    @Data
    private static class ImageData {
        private String url;
    }

    /**
     * AI API 호출 메서드: DALL-E 이미지 생성 -> 카테고리/가격 산정 순으로 진행
     * PolicyHandler에서 호출될 예정 (Mono<AIResponse> 반환으로 변경)
     *
     * @param manuscriptId 원고 ID
     * @param title        도서 제목
     * @param summary      도서 요약
     * @param keywords     도서 키워드
     * @param authorId     저자 ID
     * @param authorName   저자 이름
     * @param content      도서 상세 내용
     * @return Mono<AIResponse> AI 서비스 결과 (표지 URL, 전자책 URL, 카테고리, 가격)
     */
    public Mono<AIResponse> callGPTApi(
            Long manuscriptId, // 파라미터명 대소문자 통일 (ManuscriptId -> manuscriptId)
            String title,
            String summary,
            String keywords,
            String authorId,
            String authorName,
            String content) {

        log.info("### AI 서비스 호출 시작... ManuscriptId: {}", manuscriptId);

        // AIResponse 객체 초기화 (Reactive 체인에서 사용)
        AIResponse finalResponse = new AIResponse();
        finalResponse.setEbookUrl("https://your-domain.com/ebooks/" + manuscriptId + ".pdf"); // 전자책 URL은 바로 설정 가능

        // 1. DALL-E를 사용하여 표지 이미지 생성
        String coverPrompt = String.format(
                "\"%s\" 제목을 표지 상단에 넣고, 아래 요약에 어울리는 배경을 가진 책 앞표지 이미지를 만들어줘. 요약: %s. 키워드: %s. 상세 내용: %s",
                title, summary, keywords, content);

        return callImageGenerationApi(coverPrompt)
                .doOnNext(coverImageUrl -> { // 이미지 생성 후 URL 설정 및 로그
                    finalResponse.setCoverImageUrl(coverImageUrl);
                    log.info("### 생성된 표지 이미지 URL: {}", coverImageUrl);
                })
                .flatMap(coverImageUrl -> { // 이미지 URL을 다음 Mono 체인으로 전달
                    // 2. 카테고리 및 가격 산정 (GPT Chat Completion API 사용)
                    String chatPrompt = String.format(
                            "제목: \"%s\", 요약: \"%s\", 키워드: \"%s\", 저자 ID: \"%s\", 저자: \"%s\", 상세 내용: \"%s\"\n" +
                                    "이 책에 대해 다음을 분석하고 응답은 JSON 형식으로만 줘:\n" +
                                    "1. 가장 적합한 카테고리를 다음 중 하나로 선택해줘: 소설, 기술, 역사, 자기계발, 어린이, 문학, SF, 기타\n" +
                                    "2. 이 전자책의 예상 가격을 한국 원화(KRW)로 10000원 단위로 알려줘. (예: 25000). 책의 내용, 분량(상세 내용 기준), 일반적인 전자책 시장 가격을 고려해줘.\n" +
                                    "```json\n" +
                                    "{\n" +
                                    "   \"category\": \"선택된 카테고리\",\n" +
                                    "   \"price\": 예상 가격 (숫자만)\n" +
                                    "}\n" +
                                    "```",
                            title, summary, keywords, authorId, authorName, content);

                    return callChatCompletionApi(chatPrompt)
                            .map(chatResponse -> {
                                if (chatResponse != null && chatResponse.getChoices() != null && !chatResponse.getChoices().isEmpty()) {
                                    String gptContent = chatResponse.getChoices().get(0).getMessage().getContent();
                                    log.info("### GPT 카테고리/가격 원시 응답: {}", gptContent);

                                    try {
                                        // JSON 마크다운 블록 제거 (정규식 사용)
                                        String cleanJson = gptContent.replaceAll("```json\\s*([\\s\\S]*?)\\s*```", "$1").trim();
                                        JsonNode rootNode = objectMapper.readTree(cleanJson);

                                        if (rootNode.has("category")) {
                                            finalResponse.setCategory(rootNode.get("category").asText());
                                        } else {
                                            finalResponse.setCategory("기타");
                                        }
                                        if (rootNode.has("price")) {
                                            finalResponse.setPrice(rootNode.get("price").asInt());
                                        } else {
                                            finalResponse.setPrice(15000);
                                        }
                                    } catch (IOException e) {
                                        log.error("GPT 응답 JSON 파싱 중 오류 발생 (카테고리/가격): {}", e.getMessage(), e);
                                        finalResponse.setCategory("기타");
                                        finalResponse.setPrice(15000);
                                    }
                                } else {
                                    log.warn("GPT 카테고리/가격 응답을 받지 못했습니다. 기본값으로 설정합니다.");
                                    finalResponse.setCategory("기타");
                                    finalResponse.setPrice(15000);
                                }
                                return finalResponse; // 최종 AIResponse 반환
                            })
                            .onErrorReturn(finalResponse); // GPT API 호출 중 오류 발생 시 현재까지의 finalResponse 반환
                })
                .doOnSuccess(response -> log.info("### AI 서비스 최종 결과: {}", response)) // 최종 성공 로깅
                .onErrorResume(e -> { // 전체 AI 호출 체인 중 오류 발생 시
                    log.error("### AI 서비스 호출 중 치명적인 오류 발생: {}", e.getMessage(), e);
                    // 오류 발생 시 기본 AIResponse를 반환하거나 특정 오류 응답을 생성
                    AIResponse errorResponse = new AIResponse();
                    errorResponse.setCoverImageUrl("https://via.placeholder.com/150?text=Error");
                    errorResponse.setEbookUrl("https://your-domain.com/ebooks/error.pdf");
                    errorResponse.setCategory("오류");
                    errorResponse.setPrice(0);
                    return Mono.just(errorResponse);
                });
    }

    /**
     * GPT Chat Completion API를 호출하는 내부 메서드 (Mono<ChatCompletionResponse> 반환으로 변경)
     *
     * @param prompt 사용자 프롬프트
     * @return Mono<ChatCompletionResponse> GPT 응답 DTO를 감싼 Mono
     */
    private Mono<ChatCompletionResponse> callChatCompletionApi(String prompt) {
        ChatCompletionRequest request = new ChatCompletionRequest(
                gptModel,
                List.of(new Message("user", prompt)));

        return gptWebClient.post()
                .uri("/chat/completions")
                .body(Mono.just(request), ChatCompletionRequest.class)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse ->
                    clientResponse.bodyToMono(String.class)
                                  .flatMap(body -> Mono.error(new RuntimeException("GPT API Error: " + clientResponse.statusCode() + " - " + body)))
                )
                .bodyToMono(ChatCompletionResponse.class);
    }

    /**
     * DALL-E 3 Image Generation API를 호출하는 내부 메서드 (Mono<String> 반환으로 변경)
     *
     * @param prompt 이미지 생성 프롬프트
     * @return Mono<String> 생성된 이미지 URL을 감싼 Mono (실패 시 대체 URL)
     */
    private Mono<String> callImageGenerationApi(String prompt) {
        ImageGenerationRequest request = new ImageGenerationRequest();
        request.setModel(imageModel);
        request.setPrompt(prompt);
        request.setN(1);
        request.setSize("1024x1024");

        return imageWebClient.post()
                .uri("/images/generations")
                .body(Mono.just(request), ImageGenerationRequest.class)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse ->
                    clientResponse.bodyToMono(String.class)
                                  .flatMap(body -> Mono.error(new RuntimeException("Image API Error: " + clientResponse.statusCode() + " - " + body)))
                )
                .bodyToMono(ImageGenerationResponse.class)
                .map(response -> {
                    if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                        return response.getData().get(0).getUrl();
                    }
                    log.warn("### 이미지 생성 실패 - 프롬프트: {}. 응답 데이터 없음.", prompt);
                    return "https://via.placeholder.com/150?text=No+Image"; // 이미지 생성 실패 시 대체 URL
                })
                .onErrorReturn("https://via.placeholder.com/150?text=Error"); // API 호출 자체에서 오류 발생 시 대체 URL
    }
}