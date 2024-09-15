package ru.practicum.explore.stat.model;

import lombok.Data;

import java.sql.Timestamp;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hits")
@Data
@NoArgsConstructor
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "app", nullable = false)
    private String app;
    @Column(name = "uri", nullable = false)
    private String uri;
    @Column(name = "ip", nullable = false)
    private String ip;
    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;
}
