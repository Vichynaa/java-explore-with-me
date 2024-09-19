package ru.practicum.explore.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.explore.stat.dto.HitRequest;
import ru.practicum.explore.stat.dto.ViewStats;

import java.lang.reflect.Type;
import java.util.*;

@Service
@Slf4j
public class StatClient {
    @Autowired
    private Gson gson;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${client-server.url}")
    private String baseUrl;

    public StatClient(RestTemplateBuilder restTemplateBuilder, @Value("${client-server.url}") String serverUrl) {
        restTemplate = restTemplateBuilder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    public String createHit(HitRequest hitRequest) {
        HttpEntity<HitRequest> request = new HttpEntity<>(hitRequest);
        ResponseEntity<String> response = restTemplate.postForEntity("/hit", request, String.class);
        checkResponse(response);
        return response.getBody();
    }

    public List<ViewStats> getStat(String start, String end, List<String> uris, Optional<Boolean> isUnique) {
        String uri = UriComponentsBuilder.fromPath("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris.toArray())
                .queryParam("unique", isUnique.orElse(false))
                .toUriString().replaceAll("%20", " ");
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        checkResponse(response);
        Type viewStatListType = new TypeToken<List<ViewStats>>() {}.getType();
        return gson.fromJson(response.getBody(), viewStatListType);
    }

    private <T> void checkResponse(ResponseEntity<T> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error((String) response.getBody());
            throw new ValidationException((String) response.getBody());
        }
    }
}
