package ru.practicum.explore.event;

import client.HitRequestCreator;
import client.StatClient;
import constant.Constant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.event.dto.*;
import ru.practicum.explore.event.mappers.EventMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class EventController {
    private final EventInterface eventDbService;
    @Autowired
    private final StatClient statClient;

    @GetMapping("/events")
    public List<EventShortDto> publicFindAllByFilters(@RequestParam Optional<String> text,
                                                      @RequestParam Optional<List<Long>> categories,
                                                      @RequestParam Optional<Boolean> paid,
                                                      @RequestParam Optional<String> rangeStart,
                                                      @RequestParam Optional<String> rangeEnd,
                                                      @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                      @RequestParam Optional<String> sort,
                                                      @RequestParam(defaultValue = "0") Long from,
                                                      @RequestParam(defaultValue = "10") Long size,
                                                      HttpServletRequest request) {
        log.info("GET /events");
        createHitByRequest(request);
        return eventDbService.findAllEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                from, size).stream().map(EventMapper::mapToEventShortDto).toList();
    }

    @GetMapping("/events/{id}")
    public EventFullDto publicFindEventById(@PathVariable Long id, HttpServletRequest request) {
        log.info("GET public /events/{}", id);
        createHitByRequest(request);
        return EventMapper.mapToEventFullDto(eventDbService.findEventByEventId(id));
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> privateFindEventsByUserId(@PathVariable Long userId,
                                                         @RequestParam(defaultValue = "0") Long from,
                                                         @RequestParam(defaultValue = "10") Long size) {
        log.info("GET private users/{}/events", userId);
        return eventDbService.findAllByInitiatorId(userId, from, size)
                .stream()
                .map(EventMapper::mapToEventShortDto)
                .toList();
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto privateCreateEvent(@Valid @RequestBody NewEventDto newEventDto, @PathVariable Long userId) {
        log.info("POST private users/{}/events", userId);
        return EventMapper.mapToEventFullDto(eventDbService.create(newEventDto, userId));
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto privateFindEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("GET private /users/{}/events/{}", userId, eventId);
        return EventMapper.mapToEventFullDto(eventDbService.findEventByEventIdInitiator(userId, eventId));
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto privateUpdateEvent(@Valid @RequestBody UpdateEventUserRequest updateEventUserRequest, @PathVariable Long userId, @PathVariable Long eventId) {
        log.info("PATCH private /users/{}/events/{}", userId, eventId);
        return EventMapper.mapToEventFullDto(eventDbService.userUpdate(updateEventUserRequest, userId, eventId));
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> adminFindAllByFilters(@RequestParam Optional<List<Long>> users,
                                                     @RequestParam Optional<List<String>> states,
                                                     @RequestParam Optional<List<Long>> categories,
                                                     @RequestParam Optional<String> rangeStart,
                                                     @RequestParam Optional<String> rangeEnd,
                                                     @RequestParam(defaultValue = "0") Long from,
                                                     @RequestParam(defaultValue = "10") Long size) {
        log.info("GET admin /admin/events");
        return eventDbService.findAllEventsAdmin(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size).stream().map(EventMapper::mapToEventFullDto).toList();
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto adminUpdateEvent(@Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest, @PathVariable Long eventId) {
        log.info("PATCH admin /admin/events/{}", eventId);
        return EventMapper.mapToEventFullDto(eventDbService.adminUpdate(updateEventAdminRequest, eventId));
    }



    private void createHitByRequest(HttpServletRequest request) {
        statClient.createHit(HitRequestCreator.hitRequestCreator(request.getRemoteAddr(),
                request.getRequestURI(),
                Constant.getEWM_MAIN_SERVICE(),
                LocalDateTime.now().format(Constant.getFORMATTER())));
    }
}
