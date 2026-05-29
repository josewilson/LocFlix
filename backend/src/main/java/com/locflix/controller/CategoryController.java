package com.locflix.controller;

import com.locflix.constant.ApiConstant;
import com.locflix.dto.request.CreateCategoryRequest;
import com.locflix.dto.response.CategoryResponse;
import com.locflix.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de Categorias.
 */
@RestController
@RequestMapping(ApiConstant.CATEGORIES_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Endpoints para gerenciar categorias de filmes")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar nova categoria", description = "Cria uma nova categoria (apenas admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "409", description = "Categoria já existe")
    })
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar categoria", description = "Atualiza os dados de uma categoria (apenas admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
            @ApiResponse(responseCode = "409", description = "Nome já existe")
    })
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter categoria por ID", description = "Retorna os dados de uma categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
        CategoryResponse response = categoryService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as categorias", description = "Lista todas as categorias com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso")
    public ResponseEntity<Page<CategoryResponse>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        Page<CategoryResponse> response = categoryService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar categorias por nome", description = "Busca categorias que contêm o nome especificado")
    @ApiResponse(responseCode = "200", description = "Categorias encontradas")
    public ResponseEntity<List<CategoryResponse>> searchByName(@RequestParam String name) {
        List<CategoryResponse> response = categoryService.searchByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    @Operation(summary = "Categorias mais populares", description = "Lista as categorias com mais filmes")
    @ApiResponse(responseCode = "200", description = "Categorias mais populares retornadas")
    public ResponseEntity<List<CategoryResponse>> getMostPopular() {
        List<CategoryResponse> response = categoryService.getMostPopular();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar categoria", description = "Remove uma categoria (apenas admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
            @ApiResponse(responseCode = "409", description = "Categoria possui filmes associados")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

