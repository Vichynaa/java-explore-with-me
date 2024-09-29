package ru.practicum.explore.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.user.dto.NewUserRequest;
import ru.practicum.explore.user.dto.UserDto;
import ru.practicum.explore.user.mappers.UserMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {
    private final UserInterface userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/users")
    public UserDto create(@Valid @RequestBody NewUserRequest userRequest) {
        log.info("POST /users - с даннами: name - {}; email - {}", userRequest.getName(), userRequest.getEmail());
        return UserMapper.mapToUserDto(userService.create(userRequest));
    }

    @PatchMapping("/admin/users/{userId}")
    public UserDto update(@Valid @RequestBody NewUserRequest userRequest, @PathVariable Long userId) {
        log.info("PATCH /users/{} - с даннами: name - {}; email - {}", userId, userRequest.getName(), userRequest.getEmail());
        return UserMapper.mapToUserDto(userService.update(userRequest, userId));
    }

    @GetMapping("/admin/users")
    public List<UserDto> findAll(@RequestParam Optional<List<Long>> ids, @RequestParam(defaultValue = "0") Long from, @RequestParam(defaultValue = "10") Long size) {
        log.info("GET /users");
        return userService.findAll(ids, from, size).stream().map(UserMapper::mapToUserDto).toList();
    }

    @GetMapping("/admin/users/{userId}")
    public UserDto findUserById(@PathVariable Long userId) {
        log.info("GET /users/{}", userId);
        return UserMapper.mapToUserDto(userService.findUserById(userId));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/users/{userId}")
    public String deleteUserById(@PathVariable Long userId) {
        log.info("DELETE /users/{}", userId);
        return String.format("{\"message\":\"%s\"}", userService.deleteUserById(userId));
    }


}
