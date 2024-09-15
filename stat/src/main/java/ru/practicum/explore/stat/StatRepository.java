package ru.practicum.explore.stat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore.stat.model.EndpointHit;

import java.sql.Timestamp;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Integer> {

    @Query("SELECT h FROM EndpointHit h WHERE h.uri = ?3 AND h.timestamp > ?1 AND h.timestamp < ?2")
    List<EndpointHit> findInfoByUri(Timestamp start, Timestamp end, String uri);

    @Query("SELECT h FROM EndpointHit h WHERE h.id IN (SELECT MIN(eh.id) FROM EndpointHit eh WHERE eh.uri = ?3 AND eh.timestamp > ?1 AND eh.timestamp < ?2 GROUP BY eh.ip)")
    List<EndpointHit> findInfoByUriUnique(Timestamp start, Timestamp end, String uri);
}
