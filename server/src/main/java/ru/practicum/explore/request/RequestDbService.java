package ru.practicum.explore.request;

import exception.ApiError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.event.EventInterface;
import ru.practicum.explore.event.EventRepository;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore.request.mapper.RequestMapper;
import ru.practicum.explore.request.model.Request;
import ru.practicum.explore.user.UserInterface;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestDbService implements RequestInterface {
    private final RequestRepository requestRepository;
    private final UserInterface userDbService;
    private final EventInterface eventDbService;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Request create(Long userId, Long eventId) {
        Event event;
        try {
            event = eventDbService.findEventByEventId(eventId);
        } catch (ApiError e) {
            log.error("Not available event.");
            throw new ApiError(
                    "Not available event.",
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "CONFLICT"
            );
        }
        checkRequest(userId, event);
        Request request = new Request();
        request.setRequester(userDbService.findUserById(userId));
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        if (event.getParticipantLimit().equals(event.getConfirmedRequests()) && !event.getParticipantLimit().equals(0L)) {
            log.error("Event with id={} has reached its participant limit of {}", event.getId(), event.getParticipantLimit());
            throw new ApiError(
                    "Event has reached the maximum number of participants.",
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "CONFLICT"
            );
        }
        if (event.getRequestModeration().equals(false) || event.getParticipantLimit().equals(0L)) {
            request.setStatus("CONFIRMED");
            event.setConfirmedRequests((long) requestRepository.findAllConfirmedRequestsByEventId(eventId).size() + 1);
            eventRepository.save(event);
        } else if (event.getRequestModeration().equals(true)) {
            request.setStatus("PENDING");
        }
        return requestRepository.save(request);
    }

    @Override
    @Transactional
    public Request cancelRequest(Long userId, Long requestId) {
        Optional<Request> optRequest = requestRepository.findById(requestId);
        if (optRequest.isEmpty()) {
            log.error("Request with id=" + requestId + " was not found.");
            throw new ApiError(
                    "Request with id=" + requestId + " was not found.",
                    new ArrayList<>(),
                    "The required object was not found.",
                    "NOT_FOUND"
            );
        }
        Request newRequest = optRequest.get();
        if (!Objects.equals(newRequest.getRequester().getId(), userId)) {
            log.error("Пользователь с id - " + userId + ", не оставлял запрос с id - " + requestId);
            throw new ApiError(
                    "Пользователь с id - " + userId + ", не оставлял запрос с id - " + requestId,
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "BAD_REQUEST"
            );
        }
        newRequest.setStatus("CANCELED");
        return requestRepository.save(newRequest);
    }

    private Request getRequest(String status, Request newRequest) {
        if (!newRequest.getStatus().equals("PENDING")) {
            throw new ApiError(
                    "Status of request not eq PENDING. Value: " + newRequest.getStatus(),
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "CONFLICT"
            );
        }
        if (!(status.equals("CONFIRMED") || status.equals("REJECTED"))) {
            throw new ApiError(
                    "Status error. Value: " + status,
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "BAD_REQUEST"
            );
        }
        newRequest.setStatus(status);
        return newRequest;
    }

    @Override
    public List<Request> findAllByUserId(Long userId) {
        userDbService.findUserById(userId);
        return requestRepository.findAllByUserId(userId);
    }

    @Override
    public List<Request> findAllByUserIdAndEventId(Long userId, Long eventId) {
        eventDbService.checkInitiator(eventId, userId);
        return requestRepository.findAllByEventId(eventId);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult statusRequestUpdate(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, Long userId, Long eventId) {
        eventDbService.checkInitiator(eventId, userId);
        String status = eventRequestStatusUpdateRequest.getStatus();
        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();
        Event event = eventDbService.findEventByEventId(eventId);
        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0L)) {
            log.error("No validation needed");
            throw new ApiError(
                    "No validation needed",
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "BAD_REQUEST"
            );
        }
        if (Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())) {
            log.error("The participant limit has been reached");
            throw new ApiError(
                    "The participant limit has been reached",
                    new ArrayList<>(),
                    "For the requested operation the conditions are not met.",
                    "CONFLICT"
            );
        }
        if (status.equals("CONFIRMED") && (event.getParticipantLimit() != 0 && event.getParticipantLimit() < (event.getConfirmedRequests() + requestIds.size()))) {
            requestIds = requestIds.subList(0, (int) (event.getParticipantLimit() - event.getConfirmedRequests()));
            List<Long> rejectedRequestIds = requestIds.subList((int) (event.getParticipantLimit() - event.getConfirmedRequests()), requestIds.size());
            statusRequestUpdateFunc(rejectedRequestIds, eventId, "REJECTED");
            return statusRequestUpdateFunc(requestIds, eventId, "CONFIRMED");
        }
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = statusRequestUpdateFunc(requestIds, eventId, status);
        event.setConfirmedRequests((long) eventRequestStatusUpdateResult.getConfirmedRequests().size());
        eventRepository.save(event);
        return eventRequestStatusUpdateResult;
    }

    private void checkRequest(Long userId, Event event) {
        if (requestRepository.findRequestByUserId(userId, event.getId()).isPresent()) {
            log.error("Пользователь не может отправить повторный запрос, id - " + userId);
            throw new ApiError(
                    "Not available action. Пользователь не может отправить повторный запрос." +
                            " Value: " + userId,
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "CONFLICT"
            );
        }
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            log.error("Пользователь не может оставить запрос если является инициатором, id - " + userId);
            throw new ApiError(
                    "Not available action. Пользователь не может добавить запрос если является инициатором." +
                            " Value: " + userId,
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "CONFLICT"
            );
        }
        if (!event.getState().equals("PUBLISHED")) {
            log.error("Нельзя участвовать в неопубликованном событии");
            throw new ApiError(
                    "Not available action. Нельзя участвовать в неопубликованном событии" +
                            " Value: " + event.getId(),
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "CONFLICT"
            );
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            log.error("У события достигнут лимит запросов на участие. Value: " + event.getConfirmedRequests());
            throw new ApiError(
                    "У события достигнут лимит запросов на участие. Value: " + event.getConfirmedRequests(),
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "CONFLICT"
            );
        }
    }

    private EventRequestStatusUpdateResult statusRequestUpdateFunc(List<Long> requestsIds, Long eventId, String status) {
        List<Request> newRequests = new ArrayList<>();
        List<Request> requests = requestRepository.findAllById(requestsIds);
        for (Request request : requests) {
                Request newRequest = getRequest(status, request);
                newRequests.add(newRequest);
        }
        requestRepository.saveAll(newRequests);
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        eventRequestStatusUpdateResult.setConfirmedRequests(requestRepository.findAllConfirmedRequestsByEventId(eventId).stream().map(RequestMapper::mapToParticipationRequestDto).toList());
        eventRequestStatusUpdateResult.setRejectedRequests(requestRepository.findAllRejectedRequestsByEventId(eventId).stream().map(RequestMapper::mapToParticipationRequestDto).toList());
        return eventRequestStatusUpdateResult;
    }
}
