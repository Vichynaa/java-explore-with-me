package ru.practicum.explore.compilation.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.explore.compilation.dto.CompilationDto;
import ru.practicum.explore.compilation.model.Compilation;
import ru.practicum.explore.event.mappers.EventMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {
    public static CompilationDto mapToCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setEvents(compilation.getEvents().stream().map(EventMapper::mapToEventShortDto).toList());
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());
        return  compilationDto;
    }
}
