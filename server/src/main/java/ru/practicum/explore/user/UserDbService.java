package ru.practicum.explore.user;


import exception.ApiError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.user.dto.NewUserRequest;
import ru.practicum.explore.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserDbService implements UserInterface {
    private final UserRepository userRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public User create(NewUserRequest newUserRequest) {
        userValidation(newUserRequest.getName(), newUserRequest.getEmail());
        emailValidation(newUserRequest.getEmail());
        User user = new User();
        user.setName(newUserRequest.getName());
        user.setEmail(newUserRequest.getEmail());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(NewUserRequest newUserRequest, Long userId) {
        checkUserExistsById(userId);
        User user = userRepository.findById(userId).get();
        if (newUserRequest.getEmail() != null) {
            emailValidation(newUserRequest.getEmail());
            user.setEmail(newUserRequest.getEmail());
        }
        if (newUserRequest.getName() != null) {
            user.setName(newUserRequest.getName());
        }
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.error(String.format("Почта %s уже существует, укажите другую", user.getEmail()));
            throw new ApiError(String.format("Почта %s уже существует, укажите другую", user.getEmail()), List.of(Arrays.toString(e.getStackTrace())), "Вы используете уже существующую почту", "BAD_REQUEST", LocalDateTime.now().format(formatter));
        }
    }

    @Override
    public User findUserById(Long userId) {
        checkUserExistsById(userId);
        return userRepository.findById(userId).get();
    }

    @Override
    @Transactional
    public String deleteUserById(Long userId) {
        checkUserExistsById(userId);
        userRepository.deleteById(userId);
        log.info("Пользователь с id - " + userId + ", удалил свой аккаунт");
        return "Пользователь с id - " + userId + ", удалил свой аккаунт";
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    private void userValidation(String name, String email) {
        if (name.isBlank() || email.isBlank()) {
            log.error("Поля name и email должны быть заполнен");
            throw new ApiError("Поля name и email должны быть заполнен", new ArrayList<>(), "Вы не указали имя или почту", "BAD_REQUEST", LocalDateTime.now().format(formatter));
        }
    }

    private void emailValidation(String email) {
        if (!email.contains("@")) {
            log.error("Не правильный формат email");
            throw new ApiError("Не правильный формат email", new ArrayList<>(), "Поле email должно содержать символ @", "BAD_REQUEST", LocalDateTime.now().format(formatter));
        }
    }

    private void checkUserExistsById(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error(String.format("Пользователь с id - %d, не найден", userId));
            throw new ApiError(String.format("Пользователь с id - %d, не найден", userId), new ArrayList<>(), "Аккаунта с таким id не существует", "NOT_FOUND", LocalDateTime.now().format(formatter));
        }
    }
}
