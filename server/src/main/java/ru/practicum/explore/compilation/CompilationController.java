package ru.practicum.explore.compilation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.compilation.dto.CompilationDto;
import ru.practicum.explore.compilation.dto.NewCompilationDto;
import ru.practicum.explore.compilation.dto.UpdateCompilationRequest;
import ru.practicum.explore.compilation.mappers.CompilationMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationInterface compilationDbService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/compilations")
    public CompilationDto create(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("POST /admin/compilations - с даннами: title - {}", newCompilationDto.getTitle());
        return CompilationMapper.mapToCompilationDto(compilationDbService.create(newCompilationDto));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/compilations/{compId}")
    public String delete(@PathVariable Long compId) {
        log.info("DELETE /admin/compilations/{}", compId);
        return compilationDbService.delete(compId);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto update(@Valid @RequestBody UpdateCompilationRequest updateCompilationRequest, @PathVariable Long compId) {
        log.info("PATCH /admin/compilations/{}", compId);
        return CompilationMapper.mapToCompilationDto(compilationDbService.update(updateCompilationRequest, compId));
    }

    @GetMapping("/compilations")
    public List<CompilationDto> findAllCompilations(@RequestParam Optional<Boolean> pinned,
                                        @RequestParam(defaultValue = "0") Long from,
                                        @RequestParam(defaultValue = "10") Long size) {
        log.info("GET /compilations");
        return compilationDbService.findCompilationsByFilters(pinned, from, size).stream().map(CompilationMapper::mapToCompilationDto).toList();
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto findCompilationById(@PathVariable Long compId) {
        log.info("GET /compilations/{}", compId);
        return CompilationMapper.mapToCompilationDto(compilationDbService.findCompilationById(compId));
    }
}
