package ru.practicum.explore.stat;

import ru.practicum.explore.stat.dto.HitRequest;
import ru.practicum.explore.stat.dto.ViewStats;

import java.util.List;
import java.util.Optional;

public interface StatInterface {
    public String create(HitRequest hitRequest);

    public List<ViewStats> getStat(String start, String end, Optional<List<String>> uris, boolean unique);
}
