package ru.practicum.explore.event.comment;

import exception.ApiError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.event.EventRepository;
import ru.practicum.explore.event.comment.dto.CommentRequest;
import ru.practicum.explore.event.comment.model.Comment;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.user.UserInterface;

import java.util.ArrayList;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentDbService implements CommentInterface {
    private final CommentRepository commentRepository;
    private final UserInterface userDbService;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Comment createComment(CommentRequest commentRequest, Long userId, Long eventId) {
        checkCommentRequest(commentRequest);
        Comment comment = new Comment();
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ApiError(
                        String.format("Event with id=%d was not found", eventId),
                        new ArrayList<>(),
                        "The required object was not found.",
                        "NOT_FOUND"
                )
        );
        comment.setEvent(event);
        comment.setText(commentRequest.getText());
        comment.setAuthor(userDbService.findUserById(userId));
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment updateComment(CommentRequest commentRequest, Long userId, Long commentId) {
        checkCommentRequest(commentRequest);
        Comment comment = getCommentById(commentId);
        checkAuthor(userId, comment);
        comment.setText(commentRequest.getText());
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void delCommentAuthor(Long userId, Long commentId) {
        Comment comment = getCommentById(commentId);
        checkAuthor(userId, comment);
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void delCommentAdmin(Long commentId) {
        Comment comment = getCommentById(commentId);
        commentRepository.delete(comment);
    }

    private void checkCommentRequest(CommentRequest commentRequest) {
        if (commentRequest.getText() == null || commentRequest.getText().isBlank()) {
            throw new ApiError(
                    String.format("Error. Text for comment can not be null or empty. Value:%s", commentRequest.getText()),
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "BAD_REQUEST"
            );
        }
    }

    private void checkAuthor(Long userId, Comment comment) {
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            log.error(String.format("Only author can edit comment with id-%s", comment.getId()));
            throw new ApiError(
                    String.format("Only author can edit comment with id-%s", comment.getId()),
                    new ArrayList<>(),
                    "For the requested operation the conditions are not met.",
                    "FORBIDDEN"
            );
        }
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new ApiError(
                        String.format("Error. Comment not found. Value: commentId-%s", commentId),
                        new ArrayList<>(),
                        "The required object was not found.",
                        "NOT_FOUND"
                )
        );
    }
}
