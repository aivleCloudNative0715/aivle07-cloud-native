package aivlecloudnative.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.annotation.JsonProperty; // JSON 필드 매핑용

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service // Spring Component로 등록
public class AIServiceSystem {

    private final WebClient webClient;
    private final String gptModel;
    private final String imageModel;
    private final String openAiApiKey;

    // application.yml에서 설정 값 주입
    public AIServiceSystem(
            WebClient.Builder webClientBuilder,
            @Value("${spring.gpt.base-url}") String gptBaseUrl, // <-- 여기에 spring. 추가
            @Value("${spring.gpt.image-url}") String imageBaseUrl, // <-- 여기에 spring. 추가
            @Value("${spring.gpt.model}") String gptModel, // <-- 여기에 spring. 추가
            @Value("${spring.gpt.image-model}") String imageModel, // <-- 여기에 spring. 추가
            @Value("${spring.gpt.api-key}") String openAiApiKey) { // <-- 여기에 spring. 추가

        this.openAiApiKey = openAiApiKey;
        this.gptModel = gptModel;
        this.imageModel = imageModel;

        // GPT Chat Completion용 WebClient
        this.webClient = webClientBuilder.baseUrl(gptBaseUrl)
                .defaultHeader("Authorization", "Bearer " + openAiApiKey)
                .build();
    }

    // (나머지 코드는 동일)

    // AI 서비스 응답을 위한 내부 클래스 (최종 반환 DTO)
    @lombok.Data
    public static class AIResponse {
        private String coverImageUrl;
        private String ebookUrl; // GPT가 직접 생성하지 않으므로 임시 URL 또는 외부 URL 사용
        private String category;
        private Integer price;
    }

    // --- OpenAI Chat Completion API 요청/응답 모델 ---
    @lombok.Data
    private static class ChatCompletionRequest {
        private String model;
        private List<Message> messages;
        private double temperature = 0.7; // 창의성 조절

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

    // --- OpenAI Image Generation API 요청/응답 모델 ---
    @lombok.Data
    private static class ImageGenerationRequest {
        private String model; // dall-e-3
        private String prompt;
        private int n = 1; // 생성할 이미지 개수
        private String size = "1024x1024"; // 이미지 크기 (DALL-E 3는 1024x1024, 1024x1792, 1792x1024만 지원)
        private String quality = "standard"; // 또는 "hd"
        private String style = "vivid"; // 또는 "natural"
        @JsonProperty("response_format")
        private String responseFormat = "url"; // 또는 "b64_json"
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
    public AIResponse callGPTApi(String title, String summary, String keywords, String authorName) {
        System.out.println("### Calling AI services for book work processing...");

        AIResponse finalResponse = new AIResponse();

        // 1. 표지 이미지 생성
        String coverPrompt = String.format(
                "%s 제목을 표지 상단에 넣고, 아래 요약에 어울리는 배경을 가진 책 앞표지 이미지를 만들어줘. 요약: %s. 키워드: %s",
                title, summary, keywords);
        String coverImageUrl = callImageGenerationApi(coverPrompt);
        finalResponse.setCoverImageUrl(coverImageUrl);
        finalResponse.setEbookUrl("http://temp-ebook-url.com/" + System.currentTimeMillis() + ".pdf"); // 임시 URL

        // 2. 카테고리 및 가격 산정 (Chat Completion API 사용)
        String chatPrompt = String.format(
                "제목: \"%s\", 요약: \"%s\", 키워드: \"%s\", 저자: \"%s\" 이 책에 대해 다음을 분석해줘.\n" +
                        "1. 가장 적합한 카테고리를 다음 중 하나로 선택해줘: 소설, 기술, 역사, 자기계발, 어린이, 문학, SF, 기타\n" +
                        "2. 이 전자책의 예상 가격을 한국 원화(KRW)로 10000원 단위로 알려줘. (예: 25000원). 책의 내용, 분량(요약 기준), 그리고 일반적인 전자책 시장 가격을 고려해줘.\n"
                        +
                        "응답은 다음과 같은 JSON 형식으로만 줘:\n" +
                        "```json\n" +
                        "{\n" +
                        "   \"category\": \"선택된 카테고리\",\n" +
                        "   \"price\": \"예상 가격 (숫자만, 원화 기호 없이)\"\n" +
                        "}\n" +
                        "```",
                title, summary, keywords, authorName);

        ChatCompletionResponse chatResponse = callChatCompletionApi(chatPrompt);
        if (chatResponse != null && chatResponse.getChoices() != null && !chatResponse.getChoices().isEmpty()) {
            String content = chatResponse.getChoices().get(0).getMessage().getContent();
            System.out.println("### Raw GPT Chat Response: " + content);

            // JSON 파싱 (정규식 또는 ObjectMapper 사용)
            // 여기서는 간단히 정규식으로 파싱
            Pattern categoryPattern = Pattern.compile("\"category\"\\s*:\\s*\"([^\"]+)\"");
            Pattern pricePattern = Pattern.compile("\"price\"\\s*:\\s*\"?(\\d+)\"?"); // 숫자로만 파싱

            Matcher categoryMatcher = categoryPattern.matcher(content);
            if (categoryMatcher.find()) {
                finalResponse.setCategory(categoryMatcher.group(1));
            } else {
                finalResponse.setCategory("기타"); // 파싱 실패 시 기본값
            }

            Matcher priceMatcher = pricePattern.matcher(content);
            if (priceMatcher.find()) {
                try {
                    finalResponse.setPrice(Integer.parseInt(priceMatcher.group(1)));
                } catch (NumberFormatException e) {
                    finalResponse.setPrice(15000); // 파싱 실패 시 기본값
                }
            } else {
                finalResponse.setPrice(15000); // 파싱 실패 시 기본값
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

        return webClient.post()
                .uri("/chat/completions")
                .body(Mono.just(request), ChatCompletionRequest.class)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .block(); // 비동기 호출이지만, 여기서는 간단화를 위해 block() 사용. 실제 서비스에서는 비동기 처리 권장.
    }

    // DALL-E 3 Image Generation API 호출
    private String callImageGenerationApi(String prompt) {
        WebClient imageWebClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/images/generations") // 이미지 API 엔드포인트
                .defaultHeader("Authorization", "Bearer " + openAiApiKey)
                .build();

        ImageGenerationRequest request = new ImageGenerationRequest();
        request.setModel(imageModel);
        request.setPrompt(prompt);
        request.setN(1); // 1개의 이미지
        request.setSize("1024x1024"); // DALL-E 3 권장 크기

        ImageGenerationResponse response = imageWebClient.post()
                .uri("/") // 이미지 API의 URI는 일반적으로 베이스 URL 자체입니다.
                .body(Mono.just(request), ImageGenerationRequest.class)
                .retrieve()
                .bodyToMono(ImageGenerationResponse.class)
                .block(); // 블로킹 호출

        if (response != null && response.getData() != null && !response.getData().isEmpty()) {
            return response.getData().get(0).getUrl();
        }
        return "https://via.placeholder.com/150?text=No+Image"; // 실패 시 대체 이미지
    }
}