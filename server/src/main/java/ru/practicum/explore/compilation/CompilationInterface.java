package ru.practicum.explore.compilation;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.compilation.dto.NewCompilationDto;
import ru.practicum.explore.compilation.dto.UpdateCompilationRequest;
import ru.practicum.explore.compilation.model.Compilation;

import java.util.List;
import java.util.Optional;

public interface CompilationInterface {
    @Transactional
    Compilation create(NewCompilationDto newCompilationDto);

    @Transactional
    Compilation update(UpdateCompilationRequest updateCompilationRequest, Long compId);

    @Transactional
    String delete(Long compId);

    Compilation findCompilationById(Long compId);

    List<Compilation> findCompilationsByFilters(Optional<Boolean> pinned,
                                                Long from,
                                                Long size);
}
