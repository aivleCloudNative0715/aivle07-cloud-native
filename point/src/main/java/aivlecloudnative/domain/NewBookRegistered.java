package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NewBookRegistered extends AbstractEvent { // AbstractEvent를 상속받는다고 가정 (파일 구조에 AbstractEvent.java가 있어서)
    private String bookId;
    private String title;
    private String author;
    private Long price; // 도서 열람에 필요한 포인트 가격
    private String genre;
}