package ru.practicum.explore.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.category.dto.CategoryDto;
import ru.practicum.explore.category.dto.NewCategoryDto;
import ru.practicum.explore.category.mappers.CategoryMapper;

import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryInterface categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/categories")
    public CategoryDto create(@RequestBody NewCategoryDto newCategoryDto) {
        log.info("POST /admin/categories - с даннами: name - {}", newCategoryDto.getName());
        return CategoryMapper.mapToCategoryDto(categoryService.create(newCategoryDto));
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto update(@RequestBody NewCategoryDto newCategoryDto, @PathVariable Long catId) {
        log.info("Patch /admin/categories/{} - с даннами: name - {}", catId, newCategoryDto.getName());
        return CategoryMapper.mapToCategoryDto(categoryService.update(newCategoryDto, catId));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/categories/{catId}")
    public String delete(@PathVariable Long catId) {
        log.info("DELETE /admin/categories/{}", catId);
        return categoryService.deleteCategoryById(catId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> findAllCategories(@RequestParam(defaultValue = "0") Long from, @RequestParam(defaultValue = "10") Long size) {
        return categoryService.findAll(from, size).stream().map(CategoryMapper::mapToCategoryDto).toList();
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto findAllCategories(@PathVariable Long catId) {
        return CategoryMapper.mapToCategoryDto(categoryService.findCategoryById(catId));
    }
}
