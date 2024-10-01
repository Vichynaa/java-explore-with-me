package ru.practicum.explore.category;

import exception.ApiError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.category.dto.NewCategoryDto;
import ru.practicum.explore.category.model.Category;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryDbService implements CategoryInterface {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category create(NewCategoryDto newCategoryDto) {
        categoryValidation(newCategoryDto);
        Category category = new Category();
        category.setName(newCategoryDto.getName());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category update(NewCategoryDto newCategoryDto, Long categoryId) {
        checkCategoryExistsById(categoryId);
        categoryValidation(newCategoryDto);
        Category category = categoryRepository.findById(categoryId).get();
        category.setName(newCategoryDto.getName());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public String deleteCategoryById(Long categoryId) {
        checkCategoryExistsById(categoryId);
        checkCategoryNotHaveAnyRelatedEvents(categoryId);
        categoryRepository.deleteById(categoryId);
        log.info("Категория с id - " +  categoryId + " успешно удалена");
        return "Категория с id - " +  categoryId + " успешно удалена";
    }

    @Override
    public List<Category> findAll(Long from, Long size) {
        if (from < 0 || size < 0) {
            log.error("Параметры не могут быть отрицательными");
            throw new ApiError("Параметры не могут быть отрицательными", List.of("Параметры не могут быть отрицательными"), "Указанные парметры меньше чем 0", "CONFLICT");
        }
        return categoryRepository.findAllByFilters(from, size);
    }

    @Override
    public Category findCategoryById(Long categoryId) {
        checkCategoryExistsById(categoryId);
        return categoryRepository.findById(categoryId).get();
    }


    private void categoryValidation(NewCategoryDto newCategoryDto) {
        if (newCategoryDto.getName() == null || newCategoryDto.getName().isBlank()) {
            log.error("Имя должно быть указано");
            throw new ApiError("Field: name. Error: must not be blank. Value: null",
                    new ArrayList<>(),
                    "Incorrectly made request.",
                    "BAD_REQUEST");
        }
    }

    private void  checkCategoryExistsById(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            log.error("Не найдена категория по id - " + categoryId);
            throw new ApiError(
                    String.format("Category with id=%d was not found", categoryId),
                    new ArrayList<>(),
                    "The required object was not found.",
                    "NOT_FOUND");
        }
    }

    private void  checkCategoryNotHaveAnyRelatedEvents(Long categoryId) {
        if (categoryRepository.checkHaveAnyRelatedEvents(categoryId)) {
            log.error("Категория не пустая");
            throw new ApiError(
                    "The category is not empty",
                    new ArrayList<>(),
                    "For the requested operation the conditions are not met.",
                    "CONFLICT");
        }
    }

}
