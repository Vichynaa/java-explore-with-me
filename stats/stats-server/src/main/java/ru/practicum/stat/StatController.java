package ru.practicum.stat;

import dto.HitRequest;
import dto.ViewStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatController {
    private final StatInterface statInterface;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public String create(@RequestBody HitRequest hitRequest) {
        log.info("POST /hit");
        return statInterface.create(hitRequest);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStat(@RequestParam Optional<String> start, @RequestParam Optional<String> end, @RequestParam Optional<List<String>> uris, @RequestParam(defaultValue = "false") boolean unique) {
        log.info("GET /stats");
        return statInterface.getStat(start, end, uris, unique);
    }
}
