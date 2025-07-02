// 파일 경로: aiservice/src/main/java/aivlecloudnative/controller/TestRequestDto.java
package aivlecloudnative.controller;

import lombok.Data; // Lombok의 @Data 어노테이션을 사용하기 위해 import

// @Data 어노테이션은 getter, setter, toString, equals, hashCode 등을 자동으로 생성해줍니다.
@Data
public class TestRequestDto {
    private Long manuscriptId;
    private String title;
    private String summary;
    private String keywords;
    private String authorId; // authorId 필드 추가
    private String authorName; // authorName 필드 추가
    private String content;
    // 필요한 경우 PublicationRequested 이벤트에 있는 다른 필드도 여기에 추가할 수 있습니다.
}