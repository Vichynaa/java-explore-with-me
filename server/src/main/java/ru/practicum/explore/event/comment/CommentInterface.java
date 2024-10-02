package ru.practicum.explore.event.comment;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.event.comment.dto.CommentRequest;
import ru.practicum.explore.event.comment.model.Comment;

public interface CommentInterface {
    @Transactional
    Comment createComment(CommentRequest commentRequest, Long userId, Long eventId);

    @Transactional
    Comment updateComment(CommentRequest commentRequest, Long userId, Long commentId);

    @Transactional
    void delCommentAuthor(Long userId, Long commentId);

    @Transactional
    void delCommentAdmin(Long commentId);
}
