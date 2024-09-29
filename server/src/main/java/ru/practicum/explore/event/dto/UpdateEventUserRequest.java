package ru.practicum.explore.event.dto;

import lombok.Data;

@Data
public class UpdateEventUserRequest {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private LocationDto location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    private String title;
}
