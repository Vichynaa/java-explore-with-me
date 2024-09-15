package ru.practicum.explore.stat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.stat.dto.HitRequest;
import ru.practicum.explore.stat.dto.ViewStats;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatController {
    private final StatInterface statInterface;

    @PostMapping("/hit")
    public String create(@RequestBody HitRequest hitRequest) {
        log.info("POST /hit");
        return statInterface.create(hitRequest);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStat(@RequestParam String start, @RequestParam String end, @RequestParam Optional<List<String>> uris, @RequestParam(defaultValue = "false") boolean unique) {
        log.info("GET /stats");
        return statInterface.getStat(start, end, uris, unique);
    }
}
