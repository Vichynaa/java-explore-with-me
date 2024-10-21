package ru.practicum.explore.event.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.event.comment.dto.CommentDto;
import ru.practicum.explore.event.comment.dto.CommentRequest;
import ru.practicum.explore.event.comment.mappers.CommentMapper;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentController {
    private final CommentInterface commentDbService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/events/{eventId}/comments")
    public CommentDto createComment(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody CommentRequest commentRequest) {
        log.info("POST /users/{}/event/{}/comments", userId, eventId);
        return CommentMapper.mapToCommentDto(commentDbService.createComment(commentRequest, userId, eventId));
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId, @PathVariable Long commentId, @RequestBody CommentRequest commentRequest) {
        log.info("PATCH /users/{}/comments/{}", userId, commentId);
        return CommentMapper.mapToCommentDto(commentDbService.updateComment(commentRequest, userId, commentId));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{userId}/comments/{commentId}")
    public void delComment(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("DELETE /users/{}/comments/{}", userId, commentId);
        commentDbService.delCommentAuthor(userId, commentId);
    }

    @DeleteMapping("admin/comments/{commentId}")
    public String delCommentAdmin(@PathVariable Long commentId) {
        log.info("DELETE admin/comments/{}", commentId);
        commentDbService.delCommentAdmin(commentId);
        return String.format("Комментарий с id - %d, успешно удалён", commentId);
    }
}
