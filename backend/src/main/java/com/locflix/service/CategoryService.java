package com.locflix.service;

import com.locflix.dto.request.CreateCategoryRequest;
import com.locflix.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface de serviço para gerenciamento de Categorias.
 */
public interface CategoryService {

    /**
     * Cria uma nova categoria.
     *
     * @param request Dados da categoria
     * @return Categoria criada
     */
    CategoryResponse create(CreateCategoryRequest request);

    /**
     * Atualiza uma categoria existente.
     *
     * @param id ID da categoria
     * @param request Dados atualizados
     * @return Categoria atualizada
     */
    CategoryResponse update(Long id, CreateCategoryRequest request);

    /**
     * Obtém uma categoria por ID.
     *
     * @param id ID da categoria
     * @return Categoria encontrada
     */
    CategoryResponse getById(Long id);

    /**
     * Lista todas as categorias com paginação.
     *
     * @param pageable Informações de paginação
     * @return Página de categorias
     */
    Page<CategoryResponse> getAll(Pageable pageable);

    /**
     * Busca categorias por nome.
     *
     * @param name Nome a buscar
     * @return Lista de categorias que combinam
     */
    List<CategoryResponse> searchByName(String name);

    /**
     * Obtém categorias mais populares (com mais filmes).
     *
     * @return Lista das categorias mais populares
     */
    List<CategoryResponse> getMostPopular();

    /**
     * Deleta uma categoria.
     *
     * @param id ID da categoria a deletar
     */
    void delete(Long id);
}

