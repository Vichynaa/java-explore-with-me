package ru.practicum.explore.compilation.dto;

import lombok.Data;
import ru.practicum.explore.event.dto.EventShortDto;

import java.util.List;

@Data
public class CompilationDto {
    private List<EventShortDto> events;
    private Long id;
    private Boolean pinned;
    private String title;

}
