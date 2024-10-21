package ru.practicum.explore.event.comment.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.explore.event.comment.dto.CommentDto;
import ru.practicum.explore.event.comment.model.Comment;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthor(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        commentDto.setEvent(comment.getEvent().getId());
        return commentDto;
    }
}
