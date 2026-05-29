package com.locflix.service.impl;

import com.locflix.dto.request.CreateCategoryRequest;
import com.locflix.dto.response.CategoryResponse;
import com.locflix.exception.BusinessLogicException;
import com.locflix.exception.ResourceNotFoundException;
import com.locflix.mapper.CategoryMapper;
import com.locflix.repository.CategoryRepository;
import com.locflix.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de Categorias.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse create(CreateCategoryRequest request) {
        log.info("Creating new category: {}", request.getName());

        // Verifica se já existe uma categoria com o mesmo nome
        if (categoryRepository.existsByName(request.getName())) {
            log.warn("Category already exists: {}", request.getName());
            throw new BusinessLogicException("Categoria '" + request.getName() + "' já existe");
        }

        var category = categoryMapper.toEntity(request);
        var savedCategory = categoryRepository.save(category);

        log.info("Category created successfully with ID: {}", savedCategory.getId());
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public CategoryResponse update(Long id, CreateCategoryRequest request) {
        log.info("Updating category: {}", id);

        var category = categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Categoria", "ID", id));

        // Verifica se novo nome já existe (se diferente do atual)
        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            log.warn("Category name already exists: {}", request.getName());
            throw new BusinessLogicException("Nome '" + request.getName() + "' já existe");
        }

        categoryMapper.updateEntity(request, category);
        var updatedCategory = categoryRepository.save(category);

        log.info("Category updated successfully: {}", id);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        log.debug("Fetching category by ID: {}", id);

        var category = categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Categoria", "ID", id));

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAll(Pageable pageable) {
        log.debug("Fetching all categories with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());

        var categories = categoryRepository.findAll(pageable);
        return categories.map(categoryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> searchByName(String name) {
        log.debug("Searching categories by name: {}", name);

        var categories = categoryRepository.findByNameContaining(name);
        return categories.stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getMostPopular() {
        log.debug("Fetching most popular categories");

        var categories = categoryRepository.findMostPopular();
        return categories.stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting category: {}", id);

        var category = categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Categoria", "ID", id));

        // Verifica se a categoria tem filmes associados
        if (!category.getMovies().isEmpty()) {
            log.warn("Cannot delete category with associated movies: {}", id);
            throw new BusinessLogicException("Não é possível deletar uma categoria que possui filmes");
        }

        categoryRepository.delete(category);
        log.info("Category deleted successfully: {}", id);
    }
}

