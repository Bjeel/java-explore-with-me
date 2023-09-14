package ru.practicum.main_service.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.dto.NewCategoryDto;
import ru.practicum.main_service.category.mapper.CategoryMapper;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.category.repository.CategoryRepository;
import ru.practicum.main_service.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryServiceImpl implements CategoryService {
  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  private final String message = "Category not found";

  @Override
  @Transactional
  public CategoryDto create(NewCategoryDto newCategoryDto) {
    log.info("Add new category {}", newCategoryDto);

    return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.newCategoryDtoToCategory(newCategoryDto)));
  }

  @Override
  public List<CategoryDto> getAll(Pageable pageable) {
    log.info("Get all categories with pagination {}", pageable);

    return categoryRepository.findAll(pageable).stream()
      .map(categoryMapper::toCategoryDto)
      .collect(Collectors.toList());
  }

  @Override
  public CategoryDto getById(Long catId) {
    log.info("Get category by id {}", catId);

    Category category = categoryRepository.findById(catId)
      .orElseThrow(() -> {
        log.error(message);

        return new NotFoundException(message);
      });

    return categoryMapper.toCategoryDto(category);
  }

  @Override
  @Transactional
  public CategoryDto patch(Long catId, CategoryDto categoryDto) {
    log.info("Update category id: {}, updated fields: {}", catId, categoryDto);

    Category category = categoryRepository.findById(catId)
      .orElseThrow(() -> {
        log.error(message);

        return new NotFoundException(message);
      });

    category.setName(categoryDto.getName());
    return categoryMapper.toCategoryDto(category);
  }

  @Override
  @Transactional
  public void deleteById(Long catId) {
    log.info("Remove category id {}", catId);

    categoryRepository.findById(catId)
      .orElseThrow(() -> {
        log.error(message);

        return new NotFoundException(message);
      });

    categoryRepository.deleteById(catId);
  }

  @Override
  public Category getCategoryById(Long catId) {
    log.info("Вывод категории с id {}", catId);

    return categoryRepository.findById(catId)
      .orElseThrow(() -> {
        log.error(message);

        return new NotFoundException(message);
      });
  }
}
