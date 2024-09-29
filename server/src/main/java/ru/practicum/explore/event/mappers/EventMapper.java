package ru.practicum.explore.event.mappers;

import constant.Constant;
import exception.ApiError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.explore.category.mappers.CategoryMapper;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.event.dto.EventFullDto;
import ru.practicum.explore.event.dto.EventShortDto;
import ru.practicum.explore.event.dto.LocationDto;
import ru.practicum.explore.event.dto.NewEventDto;
import ru.practicum.explore.event.location.Location;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.user.mappers.UserMapper;
import ru.practicum.explore.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {
    public static Event mapToEvent(NewEventDto newEventDto, User user, Category category, Location location) {
        Event event = new Event();
        if (newEventDto.getDescription() == null || newEventDto.getDescription().isBlank()) {
            log.error("Поле description не может быть пустым или null");
            throw new ApiError(
                    "Field: description. Error: must not be blank. Value: " + newEventDto.getDescription(),
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "BAD_REQUEST"
            );
        }
        if (newEventDto.getAnnotation() == null || newEventDto.getAnnotation().isBlank()) {
            log.error("Поле annotation не может быть пустым или null");
            throw new ApiError(
                    "Field: annotation. Error: must not be blank. Value: " + newEventDto.getAnnotation(),
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "BAD_REQUEST"
            );
        }
        if (newEventDto.getParticipantLimit() < 0) {
            log.error("Field: participant limit. Error: must not be less than 0. Value: " + newEventDto.getParticipantLimit());
            throw new ApiError(
                    "Field: participant limit. Error: must not be less than 0. Value: " + newEventDto.getParticipantLimit(),
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "BAD_REQUEST"
            );
        }
        event.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), Constant.getFormatter()));
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(newEventDto.getDescription());
        event.setLocation(location);
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setTitle(newEventDto.getTitle());
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState("PENDING");
        event.setViews(0L);
        event.setConfirmedRequests(0L);
        return event;
    }

    public static EventShortDto mapToEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(CategoryMapper.mapToCategoryDto(event.getCategory()));
        eventShortDto.setId(event.getId());
        eventShortDto.setEventDate(event.getEventDate().format(Constant.getFormatter()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setInitiator(UserMapper.mapToUserShortDto(event.getInitiator()));
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());
        eventShortDto.setViews(event.getViews());
        return eventShortDto;
    }

    public static EventFullDto mapToEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(CategoryMapper.mapToCategoryDto(event.getCategory()));
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setCreatedOn(event.getCreatedOn().format(Constant.getFormatter()));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate().format(Constant.getFormatter()));
        eventFullDto.setInitiator(UserMapper.mapToUserShortDto(event.getInitiator()));
        eventFullDto.setLocation(mapToLocationDto(event.getLocation()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        if (event.getPublishedOn() != null) {
            eventFullDto.setPublishedOn(event.getPublishedOn().format(Constant.getFormatter()));
        }
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(event.getViews());
        return eventFullDto;
    }

    public static LocationDto mapToLocationDto(Location location) {
        LocationDto locationDto = new LocationDto();
        locationDto.setLat(location.getLat());
        locationDto.setLon(location.getLon());
        return locationDto;
    }
}
