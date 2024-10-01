package ru.practicum.explore.compilation.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class NewCompilationDto {
    @NotEmpty
    private List<Long> events;
    @NotNull
    private Boolean pinned = false;
    @NotNull
    private String title;
}
