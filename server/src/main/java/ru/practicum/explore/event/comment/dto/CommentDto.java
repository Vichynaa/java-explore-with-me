package ru.practicum.explore.event.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String text;
    private String author;
    private Long event;
    private LocalDateTime created;
}