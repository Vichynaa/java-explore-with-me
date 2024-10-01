package ru.practicum.stat.mapper;

import dto.HitRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.stat.EndpointHit;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatMapper {
    public static EndpointHit mapToEndpointHit(HitRequest hitRequest) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setIp(hitRequest.getIp());
        endpointHit.setUri(hitRequest.getUri());
        endpointHit.setApp(hitRequest.getApp());
        endpointHit.setTimestamp(Timestamp.valueOf(hitRequest.getTimestamp()));
        return endpointHit;
    }
}
