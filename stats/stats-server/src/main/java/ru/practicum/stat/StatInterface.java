package ru.practicum.stat;


import dto.HitRequest;
import dto.ViewStats;

import java.util.List;
import java.util.Optional;

public interface StatInterface {
    public String create(HitRequest hitRequest);

    public List<ViewStats> getStat(Optional<String> start, Optional<String> end, Optional<List<String>> uris, boolean unique);
}
