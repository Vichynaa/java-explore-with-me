package ru.practicum.stat;

import dto.HitRequest;
import dto.ViewStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stat.mapper.StatMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatDbService implements StatInterface {
    private final Repository statRepository;

    @Override
    @Transactional
    public String create(HitRequest hitRequest) {
        statRepository.save(StatMapper.mapToEndpointHit(hitRequest));
        return "{\n" +
                "  \"message\": \"Информация сохранена\"\n" +
                "}";
    }

    @Override
    public List<ViewStats> getStat(Optional<String> start, Optional<String> end, Optional<List<String>> uris, boolean unique) {
        List<ViewStats> infoList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Timestamp startTime = Timestamp.valueOf(LocalDateTime.parse(start.orElse(LocalDateTime.now().plusHours(1).toString()), formatter));
        Timestamp endTime = Timestamp.valueOf(LocalDateTime.parse(end.orElse(LocalDateTime.now().minusYears(10).toString()), formatter));
        if (uris.isPresent()) {
            for (String uri: uris.get()) {
                if (unique) {
                    infoList.add(convertToStat(statRepository.findInfoByUriUnique(startTime, endTime, uri), uri));
                } else {
                    infoList.add(convertToStat(statRepository.findInfoByUri(startTime, endTime, uri), uri));
                }
            }
        } else {
            if (statRepository.checkBd().isPresent()) {
                if (unique) {
                    return convertToTop2Stats(statRepository.findTopUniqueUri(startTime, endTime));
                } else {
                    return convertToTop2Stats(statRepository.findTopUri(startTime, endTime));
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
            viewStats.setApp(endpointHits.get(0).getApp());
        }
        viewStats.setHits(endpointHits.size());
        viewStats.setUri(uri);
        return viewStats;
    }

    private List<ViewStats> convertToTop2Stats(List<EndpointHit> endpointHits) {
        String firstUri = endpointHits.get(0).getUri();
        List<EndpointHit> firstEndpoint = endpointHits.stream().filter(hit -> hit.getUri().equals(firstUri)).toList();
        List<EndpointHit> secondEndpoint = endpointHits.stream().filter(hit -> !hit.getUri().equals(firstUri)).toList();

        ViewStats firstUriStat = new ViewStats();
        firstUriStat.setUri(firstUri);
        firstUriStat.setApp(firstEndpoint.get(0).getApp());
        firstUriStat.setHits(firstEndpoint.size());

        ViewStats secondUriStat = new ViewStats();
        secondUriStat.setUri(secondEndpoint.get(0).getUri());
        secondUriStat.setApp(secondEndpoint.get(0).getApp());
        secondUriStat.setHits(secondEndpoint.size());

        return Stream.of(firstUriStat, secondUriStat).sorted((Comparator.comparingInt(ViewStats::getHits))).toList();
    }
}
