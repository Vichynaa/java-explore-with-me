package ru.practicum.explore.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
