package ru.practicum.explore.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.explore.event.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    @Query(value = "SELECT * FROM events AS e WHERE e.initiator_id = ?1 LIMIT ?3 OFFSET ?2", nativeQuery = true)
    List<Event> findAllByInitiatorId(Long userId, Long from, Long size);
}
