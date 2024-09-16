package ru.practicum.explore.stat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.stat.model.EndpointHit;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface StatRepository extends JpaRepository<EndpointHit, Integer> {

    @Query("SELECT h FROM EndpointHit h WHERE h.uri = ?3 AND h.timestamp > ?1 AND h.timestamp < ?2")
    List<EndpointHit> findInfoByUri(Timestamp start, Timestamp end, String uri);

    @Query("SELECT h FROM EndpointHit h WHERE h.id IN (SELECT MIN(eh.id) FROM EndpointHit eh WHERE eh.uri = ?3 AND eh.timestamp > ?1 AND eh.timestamp < ?2 GROUP BY eh.ip)")
    List<EndpointHit> findInfoByUriUnique(Timestamp start, Timestamp end, String uri);

    @Query(value = "SELECT * FROM hits WHERE uri IN (SELECT uri FROM hits GROUP BY uri ORDER BY COUNT(uri) DESC LIMIT 2) AND timestamp BETWEEN :start AND :end", nativeQuery = true)
    List<EndpointHit> findTopUri(@Param("start") Timestamp start, @Param("end") Timestamp end);

    @Query(value = "SELECT h.* FROM hits h WHERE h.uri IN (SELECT uri FROM hits GROUP BY uri ORDER BY COUNT(uri) DESC LIMIT 2) AND EXISTS (SELECT 1 FROM hits eh WHERE eh.uri = h.uri AND eh.timestamp BETWEEN :start AND :end GROUP BY eh.ip HAVING COUNT(DISTINCT eh.ip) > 0)", nativeQuery = true)
    List<EndpointHit> findTopUniqueUri(@Param("start") Timestamp start, @Param("end") Timestamp end);

    @Query(value = "SELECT * FROM hits LIMIT 1", nativeQuery = true)
    Optional<EndpointHit> checkBd();
}
