package ru.practicum.explore.event.location;

import exception.ApiError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.event.dto.LocationDto;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LocationDbService implements LocationInterface {
    private final LocationRepository locationRepository;

    @Override
    public Location getOrCreate(LocationDto locationDto) {
        checkVariable(locationDto);
        Optional<Location> optLocation = locationRepository.findByValues(locationDto.getLat(), locationDto.getLon());
        if (optLocation.isPresent()) {
            return optLocation.get();
        }
        Location location = new Location();
        location.setLat(locationDto.getLat());
        location.setLon(locationDto.getLon());
        return locationRepository.save(location);
    }

    private void checkVariable(LocationDto locationDto) {
        if (!(Math.abs(locationDto.getLat()) <= 90 || Math.abs(locationDto.getLon()) <= 180)) {
            log.error("Долгота и широта указывают на невозможную точку на земле");
            throw new ApiError(
                    "Долгота и широта указывают на невозможную точку на земле",
                    new ArrayList<>(),
                    "Вы указали значения, которые недопустимы",
                    "CONFLICT");
        }
    }

}
