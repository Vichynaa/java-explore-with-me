package ru.practicum.explore.event.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    @Query(value = "SELECT * FROM locations AS l WHERE l.lat = ?1 AND l.lon = ?2 LIMIT 1", nativeQuery = true)
    Optional<Location> findByValues(Float lat, Float lon);
}
