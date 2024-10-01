package ru.practicum.explore.event.location;

import ru.practicum.explore.event.dto.LocationDto;

public interface LocationInterface {
    Location getOrCreate(LocationDto locationDto);
}
