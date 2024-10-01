package ru.practicum.explore.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.category.model.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "SELECT EXISTS (SELECT 1 FROM events AS e WHERE e.category_id = ?1)", nativeQuery = true)
    Boolean checkHaveAnyRelatedEvents(Long categoryId);

    @Query(value = "SELECT * FROM categories AS c WHERE c.id > ?1 LIMIT ?2", nativeQuery = true)
    List<Category> findAllByFilters(Long from, Long size);
}
