// src/main/java/aivlecloudnative/domain/NewBookRegistered.java
package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewBookRegistered extends AbstractEvent {
    private String bookId;
    private String title;
    private String author;
    private Long price;
    private String genre;
}