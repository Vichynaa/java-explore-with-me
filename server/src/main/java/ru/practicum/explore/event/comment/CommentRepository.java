package ru.practicum.explore.event.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.event.comment.model.Comment;


public interface CommentRepository extends JpaRepository<Comment, Long> {
}
