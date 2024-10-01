package ru.practicum.explore.request;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore.request.model.Request;

import java.util.List;

public interface RequestInterface {
    @Transactional
    Request create(Long userId, Long eventId);

    @Transactional
    Request cancelRequest(Long userId, Long requestId);

    List<Request> findAllByUserIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByUserId(Long userId);

    @Transactional
    EventRequestStatusUpdateResult statusRequestUpdate(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, Long userId, Long eventId);
}
