package ru.practicum.explore.request.mapper;

import constant.Constant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.explore.request.dto.ParticipationRequestDto;
import ru.practicum.explore.request.model.Request;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static ParticipationRequestDto mapToParticipationRequestDto(Request request) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setRequester(request.getRequester().getId());
        participationRequestDto.setId(request.getId());
        participationRequestDto.setStatus(request.getStatus());
        participationRequestDto.setCreated(request.getCreated().format(Constant.getFORMATTER()));
        participationRequestDto.setEvent(request.getEvent().getId());
        return participationRequestDto;
    }
}
