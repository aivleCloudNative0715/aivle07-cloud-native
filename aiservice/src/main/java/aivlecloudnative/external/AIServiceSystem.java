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
import java.util.Map;

@Service
public class AIServiceSystem {

    private final WebClient gptWebClient;
    private final WebClient imageWebClient;
    private final String gptModel;
    private final String imageModel; // DALL-E 모델 지정 (application.yml에서 설정)
    private final String openAiApiKey;
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

    // AI 서비스 응답을 위한 내부 클래스 (최종 반환 DTO)
    @lombok.Data
    public static class AIResponse {
        private String coverImageUrl;
        private String ebookUrl;
        private String category;
        private Integer price;
    }

    // --- OpenAI Chat Completion API 요청/응답 모델 (동일) ---
    @lombok.Data
    private static class ChatCompletionRequest {
        private String model;
        private List<Message> messages;
        private double temperature = 0.7;

        public ChatCompletionRequest(String model, List<Message> messages) {
            this.model = model;
            this.messages = messages;
        }
    }

    @lombok.Data
    private static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    @lombok.Data
    private static class ChatCompletionResponse {
        private List<Choice> choices;
    }

    @lombok.Data
    private static class Choice {
        private Message message;
    }

    // --- OpenAI Image Generation API 요청/응답 모델 (동일) ---
    @lombok.Data
    private static class ImageGenerationRequest {
        private String model; // dall-e-3 모델
        private String prompt;
        private int n = 1;
        private String size = "1024x1024";
        private String quality = "standard";
        private String style = "vivid";
        @JsonProperty("response_format")
        private String responseFormat = "url";
    }

    @lombok.Data
    private static class ImageGenerationResponse {
        private long created;
        private List<ImageData> data;
    }

    @lombok.Data
    private static class ImageData {
        private String url;
        @JsonProperty("b64_json")
        private String b64Json;
        private String revisedPrompt;
    }

    // 최종 GPT API 호출 메서드 (PolicyHandler에서 호출)
    // ✨ manuscriptIdId 매개변수 추가 (ebookUrl 생성을 위함)
    public AIResponse callGPTApi(Long manuscriptIdId, String title, String summary, String keywords, String authorName,
            String content) {
        System.out.println("### Calling AI services for book work processing...");

        AIResponse finalResponse = new AIResponse();

        // 1. 표지 이미지 생성
        String coverPrompt = String.format(
                "%s 제목을 표지 상단에 넣고, 아래 요약에 어울리는 배경을 가진 책 앞표지 이미지를 만들어줘. 요약: %s. 키워드: %s. 상세 내용: %s",
                title, summary, keywords, content);
        String coverImageUrl = callImageGenerationApi(coverPrompt);
        finalResponse.setCoverImageUrl(coverImageUrl);

        // ✨ ebookUrl 생성 형식 변경
        finalResponse.setEbookUrl("https://your-domain.com/ebooks/" + manuscriptIdId + ".pdf");

        // 2. 카테고리 및 가격 산정 (Chat Completion API 사용)
        String chatPrompt = String.format(
                "제목: \"%s\", 요약: \"%s\", 키워드: \"%s\", 저자: \"%s\", 상세 내용: \"%s\"\n" +
                        "이 책에 대해 다음을 분석하고 응답은 JSON 형식으로만 줘:\n" +
                        "1. 가장 적합한 카테고리를 다음 중 하나로 선택해줘: 소설, 기술, 역사, 자기계발, 어린이, 문학, SF, 기타\n" +
                        "2. 이 전자책의 예상 가격을 한국 원화(KRW)로 10000원 단위로 알려줘. (예: 25000). 책의 내용, 분량(상세 내용 기준), 일반적인 전자책 시장 가격을 고려해줘.\n"
                        +
                        "```json\n" +
                        "{\n" +
                        "  \"category\": \"선택된 카테고리\",\n" +
                        "  \"price\": 예상 가격 (숫자만)\n" +
                        "}\n" +
                        "```",
                title, summary, keywords, authorName, content);

        ChatCompletionResponse chatResponse = callChatCompletionApi(chatPrompt);
        if (chatResponse != null && chatResponse.getChoices() != null && !chatResponse.getChoices().isEmpty()) {
            String gptContent = chatResponse.getChoices().get(0).getMessage().getContent();
            System.out.println("### Raw GPT Chat Response: " + gptContent);

            try {
                JsonNode rootNode = objectMapper.readTree(gptContent);
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
                System.err.println("Error parsing GPT response JSON: " + e.getMessage());
                finalResponse.setCategory("기타");
                finalResponse.setPrice(15000);
            }
        } else {
            finalResponse.setCategory("기타");
            finalResponse.setPrice(15000);
        }

        System.out.println("### AI Service Result: " + finalResponse);
        return finalResponse;
    }

    // GPT Chat Completion API 호출
    private ChatCompletionResponse callChatCompletionApi(String prompt) {
        ChatCompletionRequest request = new ChatCompletionRequest(
                gptModel,
                List.of(new Message("user", prompt)));

        return gptWebClient.post()
                .uri("/chat/completions")
                .body(Mono.just(request), ChatCompletionRequest.class)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .block();
    }

    // DALL-E 3 Image Generation API 호출
    private String callImageGenerationApi(String prompt) {
        ImageGenerationRequest request = new ImageGenerationRequest();
        request.setModel(imageModel); // ✨ imageModel (application.yml에서 설정된 dall-e-3) 사용
        request.setPrompt(prompt);
        request.setN(1);
        request.setSize("1024x1024");

        ImageGenerationResponse response = imageWebClient.post()
                .uri("/images/generations")
                .body(Mono.just(request), ImageGenerationRequest.class)
                .retrieve()
                .bodyToMono(ImageGenerationResponse.class)
                .block();

        if (response != null && response.getData() != null && !response.getData().isEmpty()) {
            return response.getData().get(0).getUrl();
        }
        return "https://via.placeholder.com/150?text=No+Image";
    }
}