package ru.practicum.explore.event.comment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CommentRequest {
    private String text;
}