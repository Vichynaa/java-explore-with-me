package ru.practicum.explore.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore.request.dto.ParticipationRequestDto;
import ru.practicum.explore.request.mapper.RequestMapper;

import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class RequestController {
    private final RequestInterface requestDbService;

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDto> publicFindEventById(@PathVariable Long userId) {
        log.info("GET private /users/{}/requests", userId);
        return requestDbService.findAllByUserId(userId).stream().map(RequestMapper::mapToParticipationRequestDto).toList();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/requests")
    public ParticipationRequestDto create(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("POST private /users/{}/requests", userId);
        return RequestMapper.mapToParticipationRequestDto(requestDbService.create(userId, eventId));
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("PATCH private private /users/{}/requests/{}/cancel", userId, requestId);
        return RequestMapper.mapToParticipationRequestDto(requestDbService.cancelRequest(userId, requestId));
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getInfoByUserIdAndEventId(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("GET private event /users/{}/events/{}/requests", userId, eventId);
        return requestDbService.findAllByUserIdAndEventId(userId, eventId).stream().map(RequestMapper::mapToParticipationRequestDto).toList();
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusForRequests(@Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, @PathVariable Long userId, @PathVariable Long eventId) {
        log.info("PATCH private event /users/{}/events/{}/requests", userId, eventId);
        return requestDbService.statusRequestUpdate(eventRequestStatusUpdateRequest, userId, eventId);
    }
}
