package ru.practicum.explore.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import constant.Constant;
import exception.ApiError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.category.CategoryInterface;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.event.dto.NewEventDto;
import ru.practicum.explore.event.dto.UpdateEventAdminRequest;
import ru.practicum.explore.event.dto.UpdateEventUserRequest;
import ru.practicum.explore.event.location.Location;
import ru.practicum.explore.event.location.LocationInterface;
import ru.practicum.explore.event.mappers.EventMapper;
import ru.practicum.explore.event.mappers.UpdateEvent;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.event.model.QEvent;
import ru.practicum.explore.user.UserInterface;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventDbService implements EventInterface {
    private final EventRepository eventRepository;
    private final UserInterface userDbService;
    private final LocationInterface locationDbService;
    private final CategoryInterface categoryDbService;

    @Override
    @Transactional
    public Event create(NewEventDto newEventDto, Long userId) {
        eventValidation(newEventDto);
        Event event = EventMapper.mapToEvent(
                newEventDto,
                userDbService.findUserById(userId),
                categoryDbService.findCategoryById(newEventDto.getCategory()),
                locationDbService.getOrCreate(newEventDto.getLocation())
        );
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event userUpdate(UpdateEventUserRequest updateEventUserRequest, Long userId, Long eventId) {
        checkEventExists(eventId);
        checkInitiator(eventId, userId);
        checkStatus(eventId);
        Category category = null;
        Location location = null;
        if (updateEventUserRequest.getCategory() != null) {
            category = categoryDbService.findCategoryById(updateEventUserRequest.getCategory());
        }
        if (updateEventUserRequest.getLocation() != null) {
            location = locationDbService.getOrCreate(updateEventUserRequest.getLocation());
        }
        Event newEvent = UpdateEvent.updateUserEvent(
                eventRepository.findById(eventId).get(),
                updateEventUserRequest,
                category,
                location
        );
        return eventRepository.save(newEvent);
    }

    @Override
    public List<Event> findAllByInitiatorId(Long userId, Long from, Long size) {
        return eventRepository.findAllByInitiatorId(userId, from, size);
    }

    @Override
    public Event findEventByEventIdInitiator(Long userId, Long eventId) {
        checkEventExists(eventId);
        checkInitiator(eventId, userId);
        return eventRepository.findById(eventId).get();
    }

    @Override
    public Event findEventByEventId(Long eventId) {
        checkEventExists(eventId);
        BooleanExpression byStatus = QEvent.event.state.eq("PUBLISHED");
        BooleanExpression byEventId = QEvent.event.id.eq(eventId);
        Iterable<Event> itEvent = eventRepository.findAll(byEventId.and(byStatus));
        Optional<Event> eventOptional = StreamSupport.stream(itEvent.spliterator(), false)
                .findFirst();
        if (eventOptional.isEmpty()) {
            log.error("У public пользователя нету доступа к данному событию");
            throw new ApiError(
                    "Not available event.",
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "NOT_FOUND"
            );
        }
        Event event = eventOptional.get();
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);
        return event;
    }

    @Override
    public List<Event> findAllEventsAdmin(Optional<List<Long>> users,
                                          Optional<List<String>> states,
                                          Optional<List<Long>> categories,
                                          Optional<String> rangeStart,
                                          Optional<String> rangeEnd,
                                          Long from,
                                          Long size) {
        QEvent event = QEvent.event;
        BooleanExpression predicate = event.isNotNull();
        if (users.isPresent() && !users.get().isEmpty()) {
            predicate = predicate.and(event.initiator.id.in(users.get()));
        }
        if (states.isPresent() && !states.get().isEmpty()) {
            predicate = predicate.and(event.state.in(states.get()));
        }
        if (categories.isPresent() && !categories.get().isEmpty()) {
            predicate = predicate.and(event.category.id.in(categories.get()));
        }
        if (rangeStart.isPresent()) {
            predicate = predicate.and(event.eventDate.goe(LocalDateTime.parse(rangeStart.get(), Constant.getFormatter())));
        }
        if (rangeEnd.isPresent()) {
            predicate = predicate.and(event.eventDate.loe(LocalDateTime.parse(rangeEnd.get(), Constant.getFormatter())));
        }
        return eventRepository.findAll(predicate, PageRequest.of(from.intValue(), size.intValue())).toList();
    }

    @Override
    public List<Event> findAllEventsPublic(Optional<String> text,
                                           Optional<List<Long>> categories,
                                           Optional<Boolean> paid,
                                           Optional<String> rangeStart,
                                           Optional<String> rangeEnd,
                                           Boolean onlyAvailable,
                                           Optional<String> sort,
                                           Long from,
                                           Long size) {
        QEvent event = QEvent.event;
        BooleanExpression predicate = event.state.eq("PUBLISHED");

        if (text.isPresent() && !text.get().isBlank()) {
            predicate = predicate.and(event.annotation.contains(text.get())).or(event.description.contains(text.get()));
        }
        if (categories.isPresent() && !categories.get().isEmpty()) {
            predicate = predicate.and(event.category.id.in(categories.get()));
        }
        if (paid.isPresent()) {
            predicate = predicate.and(event.paid.eq(paid.get()));
        }
        if (rangeStart.isPresent() && rangeEnd.isPresent()) {
            if (LocalDateTime.parse(rangeStart.get(), Constant.getFormatter()).isAfter(LocalDateTime.parse(rangeEnd.get(), Constant.getFormatter()))) {
                log.error("Incorrectly made request, startTime before endTime. Values: " + rangeStart.get() + rangeEnd.get());
                throw new ApiError(
                        "Incorrectly made request, startTime before endTime. Values: start - "
                                + rangeStart.get() + ", end - " + rangeEnd.get(),
                        new ArrayList<>(),
                        "Incorrectly made request.",
                        "BAD_REQUEST"
                );
            }
            predicate = predicate.and(event.eventDate.goe(LocalDateTime.parse(rangeStart.get(), Constant.getFormatter())));
            predicate = predicate.and(event.eventDate.loe(LocalDateTime.parse(rangeEnd.get(), Constant.getFormatter())));
        } else {
            predicate = predicate.and(event.eventDate.goe(LocalDateTime.now()));
        }
        if (onlyAvailable) {
            predicate = predicate.and(event.confirmedRequests.goe(event.participantLimit));
        } else {
            predicate = predicate.and(event.confirmedRequests.lt(event.participantLimit));
        }
        List<Event> events = eventRepository.findAll(predicate, PageRequest.of(from.intValue(), size.intValue())).toList();
        if (sort.isPresent()) {
            if (sort.get().equals("EVENT_DATE")) {
                events = events.stream().sorted(Comparator.comparing(Event::getEventDate).reversed()).toList();
            } else if (sort.get().equals("VIEWS")) {
                events = events.stream().sorted(Comparator.comparingLong(Event::getViews).reversed()).toList();
            } else {
                log.error("Incorrectly made request, Available values for sort EVENT_DATE, VIEWS. Value: " + sort.get());
                throw new ApiError(
                        "Available values for sort EVENT_DATE, VIEWS. Value: " + sort.get(),
                        new ArrayList<>(),
                        "Incorrectly made request.",
                        "BAD_REQUEST"
                );
            }
        }
        return events;
    }

    private void checkStatus(Long eventId) {
        if (eventRepository.findById(eventId).get().getState().equals("PUBLISHED")) {
            log.error("События с id - " + eventId + ", уже опубликовано");
            throw new ApiError(
                    "Event must not be published",
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "CONFLICT"
            );
        }
    }

    @Override
    @Transactional
    public Event adminUpdate(UpdateEventAdminRequest updateEventAdminRequest, Long eventId) {
        checkEventExists(eventId);
        checkAdminStatus(eventId, updateEventAdminRequest);
        Category category = null;
        Location location = null;
        if (updateEventAdminRequest.getCategory() != null) {
            category = categoryDbService.findCategoryById(updateEventAdminRequest.getCategory());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            location = locationDbService.getOrCreate(updateEventAdminRequest.getLocation());
        }
        Event newEvent = UpdateEvent.updateAdminEvent(
                eventRepository.findById(eventId).get(),
                updateEventAdminRequest,
                category,
                location
        );
        return eventRepository.save(newEvent);
    }

    private void checkAdminStatus(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getStateAction() != null) {
            String updateState = updateEventAdminRequest.getStateAction();
            String actualState = eventRepository.findById(eventId).get().getState();
            if (updateState.equals("PUBLISH_EVENT")) {
                if (!actualState.equals("PENDING")) {
                    log.error("Событие можно публиковать, только если оно в состоянии ожидания публикации");
                    throw new ApiError(
                            "Cannot publish the event because it's not in the right state: " + actualState,
                            new ArrayList<>(),
                            "For the requested operation the conditions are not met.",
                            "CONFLICT"
                    );
                }
            }
            if (updateState.equals("REJECT_EVENT")) {
                if (actualState.equals("PUBLISHED")) {
                    log.error("Событие можно отклонить, только если оно еще не опубликовано");
                    throw new ApiError(
                            "Cannot reject the event because it's not in the right state: " + actualState,
                            new ArrayList<>(),
                            "For the requested operation the conditions are not met.",
                            "CONFLICT"
                    );
                }
            }
        }
    }

    private void eventValidation(NewEventDto newEventDto) {
        List<String> errors = List.of();
        if (newEventDto.getCategory() == null) {
            log.error("Field: location. Error: must not be blank. Value: null");
            throw new ApiError(
                    "Field: category. Error: must not be blank. Value: null",
                    errors,
                    "Incorrectly made request.",
                    "BAD_REQUEST"
            );
        }
        if (newEventDto.getEventDate() == null) {
            log.error("Field: location. Error: must not be blank. Value: null");
            throw new ApiError(
                    "Field: eventDate. Error: must not be blank. Value: null",
                    errors,
                    "Incorrectly made request.",
                    "BAD_REQUEST"
            );
        }
        LocalDateTime eventDate = LocalDateTime.parse(newEventDto.getEventDate(), Constant.getFormatter());
        if (eventDate.isBefore(LocalDateTime.now())) {
            log.error("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + newEventDto.getEventDate());
            throw new ApiError(
                    "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + newEventDto.getEventDate(),
                    errors,
                    "For the requested operation the conditions are not met.",
                    "BAD_REQUEST"
            );
        }
        if (eventDate.plusHours(2).isBefore(LocalDateTime.now())) {
            log.error("Field: eventDate. Error: дата и время на которые намечено событие не может быть раньше," +
                    " чем через два часа от текущего момента. Value: " + newEventDto.getEventDate());
            throw new ApiError(
                    "Field: eventDate. Error: дата и время на которые намечено событие не может быть раньше," +
                            " чем через два часа от текущего момента. Value: " + newEventDto.getEventDate(),
                    errors,
                    "For the requested operation the conditions are not met.",
                    "BAD_REQUEST"
            );
        }
        if (newEventDto.getLocation() == null) {
            log.error("Field: location. Error: must not be blank. Value: null");
            throw new ApiError(
                    "Field: location. Error: must not be blank. Value: null",
                    errors,
                    "Incorrectly made request.",
                    "BAD_REQUEST"
            );
        }
    }

    private void checkEventExists(Long eventId) {
        if (eventRepository.findById(eventId).isEmpty()) {
            log.error("События с id - " + eventId + ", не найдено");
            throw new ApiError(
                    String.format("Event with id=%d was not found", eventId),
                    new ArrayList<>(),
                    "The required object was not found.",
                    "NOT_FOUND"
            );
        }
    }

    @Override
    public void checkInitiator(Long eventId, Long userId) {
        if (!Objects.equals(eventRepository.findById(eventId).get().getInitiator().getId(), userId)) {
            log.error(String.format("Ползователь с id - %d, не является инициатором события с id - %d", userId, eventId));
            throw new ApiError(
                    "Only initiator can edit event with id - " + eventId,
                    new ArrayList<>(),
                    "For the requested operation the conditions are not met.",
                    "FORBIDDEN"
            );
        }
    }
}
