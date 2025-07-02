// 파일 경로: aiservice/src/main/java/aivlecloudnative/controller/TestController.java
package aivlecloudnative.controller;

import aivlecloudnative.domain.PublicationRequested; // PublicationRequested 이벤트 클래스 import
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 이 클래스가 REST 컨트롤러임을 나타냅니다.
@RestController
// 이 컨트롤러의 모든 엔드포인트는 "/test" 경로로 시작합니다.
@RequestMapping("/test")
public class TestController {

    /**
     * 테스트용 엔드포인트: PublicationRequested 이벤트를 수동으로 발행합니다.
     *
     * @param testRequestDto HTTP 요청 본문으로 받은 데이터
     * @return 이벤트 발행 성공 메시지
     */
    @PostMapping("/publish-manuscript")
    public String testPublishManuscript(@RequestBody TestRequestDto testRequestDto) {
        // 1. TestRequestDto의 데이터를 PublicationRequested 이벤트 객체로 변환
        PublicationRequested publicationRequested = new PublicationRequested();
        publicationRequested.setManuscriptId(testRequestDto.getManuscriptId());
        publicationRequested.setTitle(testRequestDto.getTitle());
        publicationRequested.setSummary(testRequestDto.getSummary());
        publicationRequested.setKeywords(testRequestDto.getKeywords());
        publicationRequested.setAuthorId(testRequestDto.getAuthorId()); // authorId 설정
        publicationRequested.setAuthorName(testRequestDto.getAuthorName()); // authorName 설정
        publicationRequested.setContent(testRequestDto.getContent());
        // 필요하다면 publicationRequested의 다른 필드도 여기서 설정할 수 있습니다.

        // 2. 이벤트를 발행합니다.
        // 이 publish() 메서드 호출이 Kafka 토픽으로 메시지를 보냅니다.
        // PolicyHandler는 이 메시지를 받아서 AIServiceSystem을 호출하게 됩니다.
        publicationRequested.publish();

        // 3. 클라이언트에게 응답 메시지 반환
        return "Test PublicationRequested event published for ManuscriptId: " + testRequestDto.getManuscriptId() +
                ". Check aiservice logs for AI processing details.";
    }
}