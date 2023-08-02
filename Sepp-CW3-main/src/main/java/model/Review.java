package model;

import java.time.LocalDateTime;

public class Review {
    private Consumer author;
    private Event event;
    private LocalDateTime creationDateTime;
    private String content;

    public Review(Consumer author, Event event, LocalDateTime creationDateTime, String content) {
        this.author = author;
        this.event = event;
        this.creationDateTime = creationDateTime;
        this.content = content;
    }

}
