package ru.practicum.explore.compilation.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore.event.model.Event;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "compilations")
@Data
@NoArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pinned", nullable = false)
    private Boolean pinned;
    @Column(name = "title", nullable = false)
    private String title;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> events = new HashSet<>();
}
