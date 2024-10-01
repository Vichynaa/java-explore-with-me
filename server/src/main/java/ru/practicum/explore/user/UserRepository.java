package ru.practicum.explore.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM users AS u WHERE u.id > ?1 LIMIT ?2", nativeQuery = true)
    List<User> findAllByFilters(Long from, Long size);

    @Query(value = "SELECT * FROM users AS u WHERE u.id IN (?1) LIMIT ?3 OFFSET ?2", nativeQuery = true)
    List<User> findAllByFiltersWithIds(List<Long> ids, Long from, Long size);
}
