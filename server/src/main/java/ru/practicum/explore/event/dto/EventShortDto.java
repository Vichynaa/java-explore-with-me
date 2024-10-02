package ru.practicum.explore.event.dto;

import lombok.Data;
import ru.practicum.explore.category.dto.CategoryDto;
import ru.practicum.explore.event.comment.dto.CommentDto;
import ru.practicum.explore.user.dto.UserShortDto;

import java.util.List;

@Data
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private String eventDate;
    private Long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
    private List<CommentDto> comments;
}
