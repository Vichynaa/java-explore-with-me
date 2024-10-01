package ru.practicum.explore.user;

import ru.practicum.explore.user.dto.NewUserRequest;
import ru.practicum.explore.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserInterface {
    User create(NewUserRequest userRequest);

    User update(NewUserRequest newUserRequest, Long userId);

    User findUserById(Long userId);

    String deleteUserById(Long userId);

    List<User> findAll(Optional<List<Long>> ids, Long from, Long size);
}
