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
        Timestamp startTime = Timestamp.valueOf(LocalDateTime.parse(start, formatter));
        Timestamp endTime = Timestamp.valueOf(LocalDateTime.parse(end, formatter));
        if (uris.isPresent()) {
            for (String uri: uris.get()) {
                if (unique) {
                    infoList.add(convertToStat(statRepository.findInfoByUriUnique(startTime, endTime, uri), Optional.ofNullable(uri)));
                } else {
                    infoList.add(convertToStat(statRepository.findInfoByUri(startTime, endTime, uri), Optional.ofNullable(uri)));
                }
            }
        } else {
            if (statRepository.checkBd().isPresent()) {
                if (unique) {
                    infoList.add(convertToStat(statRepository.findTopUniqueUri(startTime, endTime), Optional.empty()));
                    infoList.add(convertToStat(statRepository.findTopUniqueUri(startTime, endTime), Optional.empty()));
                } else {
                    infoList.add(convertToStat(statRepository.findTopUri(startTime, endTime), Optional.empty()));
                    infoList.add(convertToStat(statRepository.findTopUri(startTime, endTime), Optional.empty()));
                }
            }
        }
        return infoList.stream().sorted(Comparator.comparingInt(ViewStats::getHits).reversed()).toList();
    }

    private ViewStats convertToStat(List<EndpointHit> endpointHits, Optional<String> uri) {
        ViewStats viewStats = new ViewStats();
        if (endpointHits.isEmpty()) {
            viewStats.setApp("Не найдено информации о представленном uri");
        } else {
            viewStats.setApp(endpointHits.getFirst().getApp());
        }
        viewStats.setHits(endpointHits.size());
        if (uri.isPresent()) {
            viewStats.setUri(uri.get());
        } else {
            viewStats.setUri(endpointHits.getFirst().getUri());
        }
        return viewStats;
    }
}
