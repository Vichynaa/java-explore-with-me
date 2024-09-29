package ru.practicum.explore.category.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.explore.category.dto.CategoryDto;
import ru.practicum.explore.category.model.Category;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryMapper {
    public static CategoryDto mapToCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }
}
