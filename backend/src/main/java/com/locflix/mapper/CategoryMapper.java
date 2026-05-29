package com.locflix.mapper;

import com.locflix.dto.request.CreateCategoryRequest;
import com.locflix.dto.response.CategoryResponse;
import com.locflix.entity.Category;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Category entity e seus DTOs.
 */
@Component
public class CategoryMapper {

    /**
     * Converte um CreateCategoryRequest para uma entidade Category.
     *
     * @param request Requisição de criação
     * @return Entidade Category
     */
    public Category toEntity(CreateCategoryRequest request) {
        return Category.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .build();
    }

    /**
     * Converte um CreateCategoryRequest para atualizar uma entidade Category existente.
     *
     * @param request Requisição de atualização
     * @param category Category existente a ser atualizado
     * @return Entidade Category atualizada
     */
    public Category updateEntity(CreateCategoryRequest request, Category category) {
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        return category;
    }

    /**
     * Converte uma entidade Category para CategoryResponse.

     *
     * @param category Entidade Category
     * @return DTO de resposta
     */
    public CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}

