package ru.practicum.explore.event.mappers;

import constant.Constant;
import exception.ApiError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.event.dto.UpdateEventAdminRequest;
import ru.practicum.explore.event.dto.UpdateEventUserRequest;
import ru.practicum.explore.event.location.Location;
import ru.practicum.explore.event.model.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateEvent {
    public static Event updateUserEvent(Event event, UpdateEventUserRequest updateEventUserRequest, Category category, Location location) {
        if (updateEventUserRequest.getParticipantLimit() != null && updateEventUserRequest.getParticipantLimit() < 0) {
            log.error("Field: participant limit. Error: must not be less than 0. Value: " + updateEventUserRequest.getParticipantLimit());
            throw new ApiError(
                    "Field: participant limit. Error: must not be less than 0. Value: " + updateEventUserRequest.getParticipantLimit(),
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "BAD_REQUEST"
            );
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            if (updateEventUserRequest.getAnnotation().isBlank()) {
                log.error("Поле: annotation не может быть пустым");
                throw new ApiError(
                        "Поле: annotation не может быть пустым",
                        new ArrayList<>(),
                        "For the requested operation the conditions are not met.",
                        "BAD_REQUEST"
                );
            }
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(category);
        }
        if (updateEventUserRequest.getDescription() != null) {
            if (updateEventUserRequest.getDescription().isBlank()) {
                log.error("Поле: description не может быть пустым");
                throw new ApiError(
                        "Поле: description не может быть пустым",
                        new ArrayList<>(),
                        "For the requested operation the conditions are not met.",
                        "BAD_REQUEST"
                );
            }
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            LocalDateTime eventDate = LocalDateTime.parse(updateEventUserRequest.getEventDate(), Constant.getFormatter());
            if (eventDate.plusHours(2).isBefore(LocalDateTime.now())) {
                log.error("Field: eventDate. Error: дата и время на которые намечено событие не может быть раньше," +
                        " чем через два часа от текущего момента. Value: " + updateEventUserRequest.getEventDate());
                throw new ApiError(
                        "Field: eventDate. Error: дата и время на которые намечено событие не может быть раньше," +
                                " чем через два часа от текущего момента. Value: " + updateEventUserRequest.getEventDate(),
                        new ArrayList<>(),
                        "For the requested operation the conditions are not met.",
                        "BAD_REQUEST"
                );
            }
            event.setEventDate(eventDate);
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(location);
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        String stateAction = updateEventUserRequest.getStateAction();
        if (stateAction != null) {
            if (!(stateAction.equals("SEND_TO_REVIEW") || stateAction.equals("CANCEL_REVIEW"))) {
                log.error("Статуса: " + stateAction + ", не существует");
                throw new ApiError(
                        "Incorrect value for status. Value: " + stateAction,
                        new ArrayList<>(),
                        "For the requested operation the conditions are not met.",
                        "BAD_REQUEST"
                );
            }
            if (stateAction.equals("CANCEL_REVIEW")) {
                event.setState("CANCELED");
            } else {
                event.setState("PENDING");
            }

        }
        if (updateEventUserRequest.getTitle() != null) {
            if (updateEventUserRequest.getTitle().isBlank()) {
                log.error("Поле: title не может быть пустым");
                throw new ApiError(
                        "Поле: title не может быть пустым",
                        new ArrayList<>(),
                        "For the requested operation the conditions are not met.",
                        "BAD_REQUEST"
                );
            }
            event.setTitle(updateEventUserRequest.getTitle());
        }
        return event;
    }

    public static Event updateAdminEvent(Event event, UpdateEventAdminRequest updateEventAdminRequest, Category category, Location location) {
        if (updateEventAdminRequest.getParticipantLimit() != null && updateEventAdminRequest.getParticipantLimit() < 0) {
            log.error("Field: participant limit. Error: must not be less than 0. Value: " + updateEventAdminRequest.getParticipantLimit());
            throw new ApiError(
                    "Field: participant limit. Error: must not be less than 0. Value: " + updateEventAdminRequest.getParticipantLimit(),
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "BAD_REQUEST"
            );
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            if (updateEventAdminRequest.getAnnotation().isBlank()) {
                log.error("Поле: annotation не может быть пустым");
                throw new ApiError(
                        "Поле: annotation не может быть пустым",
                        new ArrayList<>(),
                        "For the requested operation the conditions are not met.",
                        "BAD_REQUEST"
                );
            }
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (category != null) {
            event.setCategory(category);
        }
        if (updateEventAdminRequest.getDescription() != null) {
            if (updateEventAdminRequest.getDescription().isBlank()) {
                log.error("Поле: description не может быть пустым");
                throw new ApiError(
                        "Поле: description не может быть пустым",
                        new ArrayList<>(),
                        "For the requested operation the conditions are not met.",
                        "BAD_REQUEST"
                );
            }
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            LocalDateTime eventDate = LocalDateTime.parse(updateEventAdminRequest.getEventDate(), Constant.getFormatter());
            if (eventDate.plusHours(1).isBefore(LocalDateTime.now())) {
                log.error("Field: eventDate. Error: дата и время на которые намечено событие не может быть раньше," +
                        " чем через час от текущего момента. Value: " + updateEventAdminRequest.getEventDate());
                throw new ApiError(
                        "Field: eventDate. Error: дата и время на которые намечено событие не может быть раньше," +
                                " чем через час от текущего момента. Value: " + updateEventAdminRequest.getEventDate(),
                        new ArrayList<>(),
                        "For the requested operation the conditions are not met.",
                        "BAD_REQUEST"
                );
            }
            event.setEventDate(eventDate);
        }
        if (location != null) {
            event.setLocation(location);
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        String stateAction = updateEventAdminRequest.getStateAction();
        if (stateAction != null) {
            if (!(stateAction.equals("PUBLISH_EVENT") || stateAction.equals("REJECT_EVENT"))) {
                log.error("Статуса: " + stateAction + ", не существует");
                throw new ApiError(
                        "Incorrect value for status. Value: " + stateAction,
                        new ArrayList<>(),
                        "For the requested operation the conditions are not met.",
                        "BAD_REQUEST"
                );
            }
            if (stateAction.equals("PUBLISH_EVENT")) {
                event.setState("PUBLISHED");
                event.setPublishedOn(LocalDateTime.now());
            } else {
                event.setState("CANCELED");
            }

        }
        if (updateEventAdminRequest.getTitle() != null) {
            if (updateEventAdminRequest.getTitle().isBlank()) {
                log.error("Поле: title не может быть пустым");
                throw new ApiError(
                        "Поле: title не может быть пустым",
                        new ArrayList<>(),
                        "For the requested operation the conditions are not met.",
                        "BAD_REQUEST"
                );
            }
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        return event;
    }
}
