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

import lombok.Data; // Lombok Data 어노테이션 사용

@Service
public class AIServiceSystem {

    private final WebClient gptWebClient;
    private final WebClient imageWebClient;
    private final String gptModel;
    private final String imageModel; // DALL-E 모델 지정 (application.yml에서 설정)
    private final String openAiApiKey;
    private final ObjectMapper objectMapper;

    // 생성자: WebClient와 API 키, 모델 정보 주입
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

        // GPT API 호출용 WebClient 초기화
        this.gptWebClient = webClientBuilder.baseUrl(gptBaseUrl)
                .defaultHeader("Authorization", "Bearer " + openAiApiKey)
                .build();

        // 이미지 API 호출용 WebClient 초기화
        this.imageWebClient = webClientBuilder.baseUrl(imageBaseUrl)
                .defaultHeader("Authorization", "Bearer " + openAiApiKey)
                .build();
    }

    // AI 서비스 응답을 위한 최종 DTO (데이터 전송 객체)
    @lombok.Data
    public static class AIResponse {
        private String coverImageUrl; // 표지 이미지 URL
        private String ebookUrl; // 전자책 URL
        private String category; // 카테고리
        private Integer price; // 가격
    }

    // --- OpenAI Chat Completion API 요청/응답 모델 ---
    // Chat Completion 요청 시 사용될 데이터 구조
    @lombok.Data
    private static class ChatCompletionRequest {
        private String model; // 사용할 GPT 모델명
        private List<Message> messages; // 메시지 목록
        private double temperature = 0.7; // 창의성 조절 (기본값)

        public ChatCompletionRequest(String model, List<Message> messages) {
            this.model = model;
            this.messages = messages;
        }
    }

    // 메시지 역할(role)과 내용(content)을 담는 데이터 구조
    @lombok.Data
    private static class Message {
        private String role; // 역할 (예: "user", "assistant", "system")
        private String content; // 메시지 내용

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    // Chat Completion 응답 시 사용될 데이터 구조
    @lombok.Data
    private static class ChatCompletionResponse {
        private List<Choice> choices; // 선택지 목록
    }

    // 선택지 하나를 나타내는 데이터 구조
    @lombok.Data
    private static class Choice {
        private Message message; // 메시지 내용
    }

    // --- OpenAI Image Generation API 요청/응답 모델 ---
    // Image Generation 요청 시 사용될 데이터 구조
    @lombok.Data
    private static class ImageGenerationRequest {
        private String model; // 사용할 이미지 모델 (예: dall-e-3)
        private String prompt; // 이미지 생성 프롬프트
        private int n = 1; // 생성할 이미지 개수 (기본 1개)
        private String size = "1024x1024"; // 이미지 크기
        private String quality = "standard"; // 이미지 품질 (DALL-E 3 전용)
        private String style = "vivid"; // 이미지 스타일 (DALL-E 3 전용)
        @JsonProperty("response_format") // JSON 직렬화 시 필드명 매핑
        private String responseFormat = "url"; // 응답 형식 (URL 또는 base64)
    }

    // Image Generation 응답 시 사용될 데이터 구조
    @lombok.Data
    private static class ImageGenerationResponse {
        private long created; // 생성 시간 타임스탬프
        private List<ImageData> data; // 이미지 데이터 목록
    }

    // 이미지 데이터를 담는 내부 클래스 (b64_json, revisedPrompt 필드 제거됨)
    @lombok.Data
    private static class ImageData {
        private String url; // 생성된 이미지 URL
    }

    /**
     * AI API 호출 메서드: DALL-E 이미지 생성 -> 카테고리/가격 산정 순으로 진행
     * PolicyHandler에서 호출될 예정
     *
     * @param ManuscriptId 원고 ID
     * @param title        도서 제목
     * @param summary      도서 요약
     * @param keywords     도서 키워드
     * @param authorId     저자 ID (새로 추가됨)
     * @param authorName   저자 이름
     * @param content      도서 상세 내용
     * @return AIResponse AI 서비스 결과 (표지 URL, 전자책 URL, 카테고리, 가격)
     */
    public AIResponse callGPTApi(
            Long ManuscriptId,
            String title,
            String summary,
            String keywords,
            String authorId, // <--- authorId 파라미터 추가
            String authorName,
            String content) {

        System.out.println("### AI 서비스 호출 시작...");

        AIResponse finalResponse = new AIResponse();

        // 1. **DALL-E를 사용하여 표지 이미지 생성 (전달받은 summary를 기반으로)**
        String coverPrompt = String.format(
                "\"%s\" 제목을 표지 상단에 넣고, 아래 요약에 어울리는 배경을 가진 책 앞표지 이미지를 만들어줘. 요약: %s. 키워드: %s. 상세 내용: %s",
                title, summary, keywords, content);

        String coverImageUrl = callImageGenerationApi(coverPrompt);
        finalResponse.setCoverImageUrl(coverImageUrl);
        System.out.println("### 생성된 표지 이미지 URL: " + coverImageUrl);

        // 2. **전자책 URL 생성** (ManuscriptId 활용)
        finalResponse.setEbookUrl("https://your-domain.com/ebooks/" + ManuscriptId + ".pdf");
        System.out.println("### 생성된 전자책 URL: " + finalResponse.getEbookUrl());

        // 3. **카테고리 및 가격 산정 (GPT Chat Completion API 사용)**
        String chatPrompt = String.format(
                "제목: \"%s\", 요약: \"%s\", 키워드: \"%s\", 저자 ID: \"%s\", 저자: \"%s\", 상세 내용: \"%s\"\n" + // <--- 저자 ID 프롬프트에
                                                                                                    // 추가
                        "이 책에 대해 다음을 분석하고 응답은 JSON 형식으로만 줘:\n" +
                        "1. 가장 적합한 카테고리를 다음 중 하나로 선택해줘: 소설, 기술, 역사, 자기계발, 어린이, 문학, SF, 기타\n" +
                        "2. 이 전자책의 예상 가격을 한국 원화(KRW)로 10000원 단위로 알려줘. (예: 25000). 책의 내용, 분량(상세 내용 기준), 일반적인 전자책 시장 가격을 고려해줘.\n"
                        +
                        "```json\n" +
                        "{\n" +
                        "   \"category\": \"선택된 카테고리\",\n" +
                        "   \"price\": 예상 가격 (숫자만)\n" +
                        "}\n" +
                        "```",
                title, summary, keywords, authorId, authorName, content); // <--- authorId 파라미터 전달

        ChatCompletionResponse chatResponse = callChatCompletionApi(chatPrompt);
        if (chatResponse != null && chatResponse.getChoices() != null && !chatResponse.getChoices().isEmpty()) {
            String gptContent = chatResponse.getChoices().get(0).getMessage().getContent();
            System.out.println("### GPT 카테고리/가격 원시 응답: " + gptContent);

            try {
                JsonNode rootNode = objectMapper.readTree(gptContent);
                if (rootNode.has("category")) {
                    finalResponse.setCategory(rootNode.get("category").asText());
                } else {
                    finalResponse.setCategory("기타"); // 카테고리 파싱 실패 시 기본값
                }
                if (rootNode.has("price")) {
                    finalResponse.setPrice(rootNode.get("price").asInt());
                } else {
                    finalResponse.setPrice(15000); // 가격 파싱 실패 시 기본값
                }
            } catch (IOException e) {
                System.err.println("GPT 응답 JSON 파싱 중 오류 발생 (카테고리/가격): " + e.getMessage());
                finalResponse.setCategory("기타");
                finalResponse.setPrice(15000);
            }
        } else {
            System.err.println("GPT 카테고리/가격 응답을 받지 못했습니다. 기본값으로 설정합니다.");
            finalResponse.setCategory("기타");
            finalResponse.setPrice(15000);
        }

        System.out.println("### AI 서비스 최종 결과: " + finalResponse);
        return finalResponse;
    }

    /**
     * GPT Chat Completion API를 호출하는 내부 메서드
     *
     * @param prompt 사용자 프롬프트
     * @return ChatCompletionResponse GPT 응답 DTO
     */
    private ChatCompletionResponse callChatCompletionApi(String prompt) {
        ChatCompletionRequest request = new ChatCompletionRequest(
                gptModel,
                List.of(new Message("user", prompt)));

        // WebClient를 사용하여 POST 요청 및 응답 블로킹 (동기 방식)
        return gptWebClient.post()
                .uri("/chat/completions")
                .body(Mono.just(request), ChatCompletionRequest.class)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .block(); // 블로킹 호출
    }

    /**
     * DALL-E 3 Image Generation API를 호출하는 내부 메서드
     *
     * @param prompt 이미지 생성 프롬프트
     * @return String 생성된 이미지 URL (실패 시 플레이스홀더 URL)
     */
    private String callImageGenerationApi(String prompt) {
        ImageGenerationRequest request = new ImageGenerationRequest();
        request.setModel(imageModel); // application.yml에서 설정된 dall-e-3 사용
        request.setPrompt(prompt);
        request.setN(1);
        request.setSize("1024x1024");
        // quality, style, responseFormat은 DTO에서 기본값으로 설정되어 있음

        // WebClient를 사용하여 POST 요청 및 응답 블로킹 (동기 방식)
        ImageGenerationResponse response = imageWebClient.post()
                .uri("/images/generations")
                .body(Mono.just(request), ImageGenerationRequest.class)
                .retrieve()
                .bodyToMono(ImageGenerationResponse.class)
                .block(); // 블로킹 호출

        if (response != null && response.getData() != null && !response.getData().isEmpty()) {
            return response.getData().get(0).getUrl(); // 첫 번째 이미지 URL 반환
        }
        System.err.println("### 이미지 생성 실패 - 프롬프트: " + prompt);
        return "https://via.placeholder.com/150?text=No+Image"; // 이미지 생성 실패 시 대체 URL
    }
}