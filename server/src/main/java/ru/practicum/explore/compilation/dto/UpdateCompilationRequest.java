package ru.practicum.explore.compilation.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCompilationRequest {
    @NotEmpty
    private List<Long> events;
    private Boolean pinned;
    @Size(max = 50, min = 1)
    private String title;
}
