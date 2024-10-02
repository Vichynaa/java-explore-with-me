package ru.practicum.explore.event;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.event.dto.NewEventDto;
import ru.practicum.explore.event.dto.UpdateEventAdminRequest;
import ru.practicum.explore.event.dto.UpdateEventUserRequest;
import ru.practicum.explore.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventInterface {
    @Transactional
    Event create(NewEventDto newEventDto, Long userId);

    @Transactional
    Event userUpdate(UpdateEventUserRequest updateEventUserRequest, Long userId, Long eventId);

    List<Event> findAllByInitiatorId(Long userId, Long from, Long size);

    Event findEventByEventIdInitiator(Long userId, Long eventId);

    Event findEventByEventId(Long eventId);

    List<Event> findAllEventsAdmin(Optional<List<Long>> users,
                                   Optional<List<String>> states,
                                   Optional<List<Long>> categories,
                                   Optional<String> rangeStart,
                                   Optional<String> rangeEnd,
                                   Long from,
                                   Long size);

    List<Event> findAllEventsPublic(Optional<String> text,
                                    Optional<List<Long>> categories,
                                    Optional<Boolean> paid,
                                    Optional<String> rangeStart,
                                    Optional<String> rangeEnd,
                                    Boolean onlyAvailable,
                                    Optional<String> sort,
                                    Long from,
                                    Long size);

    @Transactional
    Event adminUpdate(UpdateEventAdminRequest updateEventAdminRequest, Long eventId);

    void checkEventExists(Long eventId);

    void checkInitiator(Long eventId, Long userId);
}
