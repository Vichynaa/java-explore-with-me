package ru.practicum.explore.user;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.user.dto.NewUserRequest;
import ru.practicum.explore.user.model.User;

import java.util.List;

public interface UserInterface {
    User create(NewUserRequest userRequest);

    User update(NewUserRequest newUserRequest, Long userId);

    User findUserById(Long userId);

    String deleteUserById(Long userId);

    List<User> findAll();
}
