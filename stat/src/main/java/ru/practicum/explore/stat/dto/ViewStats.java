package ru.practicum.explore.stat.dto;

import lombok.Data;

@Data
public class ViewStats {
    private String app;
    private String uri;
    private int hits;
}
