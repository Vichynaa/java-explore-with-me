package ru.practicum.explore.event.dto;

import lombok.Data;

@Data
public class NewEventDto {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private LocationDto location;
    private Boolean paid = false;
    private Long participantLimit = 0L;
    private Boolean requestModeration = true;
    private String title;
}
