package ru.practicum.explore.stat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.stat.dto.HitRequest;
import ru.practicum.explore.stat.dto.ViewStats;
import ru.practicum.explore.stat.mapper.StatMapper;
import ru.practicum.explore.stat.model.EndpointHit;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatDbService implements StatInterface {
    private final StatRepository statRepository;

    @Override
    @Transactional
    public String create(HitRequest hitRequest) {
        statRepository.save(StatMapper.mapToEndpointHit(hitRequest));
        return "{\n" +
                "  \"message\": \"Информация сохранена\"\n" +
                "}";
    }

    @Override
    public List<ViewStats> getStat(String start, String end, Optional<List<String>> uris, boolean unique) {
        List<ViewStats> infoList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (uris.isPresent()) {
            for (String uri: uris.get()) {
                Timestamp startTime = Timestamp.valueOf(LocalDateTime.parse(start, formatter));
                Timestamp endTime = Timestamp.valueOf(LocalDateTime.parse(end, formatter));
                if (unique) {
                    infoList.add(convertToStat(statRepository.findInfoByUriUnique(startTime, endTime, uri), uri));
                } else {
                    infoList.add(convertToStat(statRepository.findInfoByUri(startTime, endTime, uri), uri));
                }
            }
        }
        return infoList.stream().sorted(Comparator.comparingInt(ViewStats::getHits).reversed()).toList();
    }

    private ViewStats convertToStat(List<EndpointHit> endpointHits, String uri) {
        ViewStats viewStats = new ViewStats();
        if (endpointHits.isEmpty()) {
            viewStats.setApp("Не найдено информации о представленном uri");
        } else {
            viewStats.setApp(endpointHits.getFirst().getApp());
        }
        viewStats.setHits(endpointHits.size());
        viewStats.setUri(uri);
        return viewStats;
    }
}
