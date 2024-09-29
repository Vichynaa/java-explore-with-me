package ru.practicum.explore.request.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    @Column(name = "status", nullable = false)
    private String status;
}
