package ru.practicum.explore.compilation;

import com.querydsl.core.types.dsl.BooleanExpression;
import exception.ApiError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.compilation.dto.NewCompilationDto;
import ru.practicum.explore.compilation.dto.UpdateCompilationRequest;
import ru.practicum.explore.compilation.model.Compilation;
import ru.practicum.explore.compilation.model.QCompilation;
import ru.practicum.explore.event.EventRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompilationDbService implements CompilationInterface {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Compilation create(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        if (newCompilationDto.getTitle() == null || newCompilationDto.getTitle().isBlank()) {
            log.error("Title can not be null or empty");
            throw new ApiError(
                    "Title can not be empty for compilation",
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "BAD_REQUEST"
                    );
        }
        compilation.setTitle(newCompilationDto.getTitle());
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            compilation.setEvents(new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents())));
        }
        compilation.setPinned(newCompilationDto.getPinned());
        return compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public Compilation update(UpdateCompilationRequest updateCompilationRequest, Long compId) {
        Compilation compilation = findCompilationById(compId);
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            compilation.setEvents(new HashSet<>(eventRepository.findAllById(updateCompilationRequest.getEvents())));
        }
        return compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public String delete(Long compId) {
        Compilation compilation = findCompilationById(compId);
        compilationRepository.delete(compilation);
        return "Подборка с id - " + compId + ", удалена успешно.";
    }

    @Override
    public Compilation findCompilationById(Long compId) {
        Optional<Compilation> optCompilation = compilationRepository.findById(compId);
        if (optCompilation.isEmpty()) {
            log.error("Now found compilation with id - " + compId);
            throw new ApiError(
                    "Compilation with id=" + compId + "was not found",
                    new ArrayList<>(),
                    "The required object was not found.",
                    "NOT_FOUND"
            );
        }
        return optCompilation.get();
    }

    @Override
    public List<Compilation> findCompilationsByFilters(Optional<Boolean> pinned,
                                                       Long from,
                                                       Long size) {
        QCompilation compilation = QCompilation.compilation;
        BooleanExpression predicate = compilation.isNotNull();
        if (pinned.isPresent()) {
            predicate = predicate.and(compilation.pinned.eq(pinned.get()));
        }
        return compilationRepository.findAll(predicate, PageRequest.of(from.intValue(), size.intValue())).toList();
    }
}
