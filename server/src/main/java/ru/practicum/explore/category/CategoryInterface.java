package ru.practicum.explore.category;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.category.dto.NewCategoryDto;
import ru.practicum.explore.category.model.Category;

import java.util.List;

public interface CategoryInterface {
    @Transactional
    Category create(NewCategoryDto newCategoryDto);

    @Transactional
    Category update(NewCategoryDto newCategoryDto, Long categoryId);

    @Transactional
    String deleteCategoryById(Long categoryId);

    List<Category> findAll(Long from, Long size);

    Category findCategoryById(Long categoryId);
}
