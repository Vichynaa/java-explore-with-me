package ru.practicum.explore.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("SELECT 1 FROM Request r WHERE r.requester.id = ?1 AND r.event.id = ?2")
    Optional<Request> findRequestByUserId(Long userId, Long eventId);

    @Query("SELECT r FROM Request r WHERE r.requester.id = ?1")
    List<Request> findAllByUserId(Long userId);

    @Query("SELECT r FROM Request r WHERE r.event.id = ?1")
    List<Request> findAllByEventId(Long eventId);

    @Query("SELECT 1 FROM Request r WHERE r.requester.id = ?1 AND r.event.id = ?2")
    Optional<Request> findByUserIdAndEventId(Long userId, Long eventId);

    @Query("SELECT r FROM Request r WHERE r.event.id = ?1 AND r.status = 'CONFIRMED'")
    List<Request> findAllConfirmedRequestsByEventId(Long eventId);

    @Query("SELECT r FROM Request r WHERE r.event.id = ?1 AND r.status = 'REJECTED'")
    List<Request> findAllRejectedRequestsByEventId(Long eventId);
}
