package ru.practicum.explore.event.dto;

import lombok.Data;
import ru.practicum.explore.category.dto.CategoryDto;
import ru.practicum.explore.event.comment.dto.CommentDto;
import ru.practicum.explore.user.dto.UserShortDto;

import java.util.List;


@Data
public class EventFullDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private UserShortDto initiator;
    private LocationDto location;
    private Boolean paid;
    private Long participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private String state;
    private String title;
    private Long views;
    private List<CommentDto> comments;
}
