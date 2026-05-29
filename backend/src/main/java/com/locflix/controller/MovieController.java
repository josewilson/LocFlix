package com.locflix.controller;

import com.locflix.constant.ApiConstant;
import com.locflix.dto.request.CreateMovieRequest;
import com.locflix.dto.response.ExternalMovieResponse;
import com.locflix.dto.response.MovieResponse;
import com.locflix.service.MovieService;
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
 * Controller REST para gerenciamento de Filmes.
 */
@RestController
@RequestMapping(ApiConstant.MOVIES_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Filmes", description = "Endpoints para gerenciar o catálogo de filmes")
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar novo filme", description = "Cria um novo filme no catálogo (apenas admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Filme criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "409", description = "Filme já existe")
    })
    public ResponseEntity<MovieResponse> create(@Valid @RequestBody CreateMovieRequest request) {
        MovieResponse response = movieService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar filme", description = "Atualiza os dados de um filme (apenas admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filme atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    })
    public ResponseEntity<MovieResponse> update(@PathVariable Long id, @Valid @RequestBody CreateMovieRequest request) {
        MovieResponse response = movieService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter filme por ID", description = "Retorna os dados completos de um filme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filme encontrado"),
            @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    })
    public ResponseEntity<MovieResponse> getById(@PathVariable Long id) {
        MovieResponse response = movieService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os filmes", description = "Lista todos os filmes com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de filmes retornada com sucesso")
    public ResponseEntity<Page<MovieResponse>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        Page<MovieResponse> response = movieService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/title")
    @Operation(summary = "Buscar filmes por título", description = "Busca filmes que contêm o título especificado")
    @ApiResponse(responseCode = "200", description = "Filmes encontrados")
    public ResponseEntity<Page<MovieResponse>> searchByTitle(
            @RequestParam String title,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<MovieResponse> response = movieService.searchByTitle(title, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/genre")
    @Operation(summary = "Buscar filmes por gênero", description = "Busca filmes de um gênero específico")
    @ApiResponse(responseCode = "200", description = "Filmes encontrados")
    public ResponseEntity<Page<MovieResponse>> searchByGenre(
            @RequestParam String genre,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<MovieResponse> response = movieService.searchByGenre(genre, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    @Operation(summary = "Listar filmes disponíveis", description = "Lista apenas os filmes disponíveis para locação")
    @ApiResponse(responseCode = "200", description = "Filmes disponíveis retornados")
    public ResponseEntity<Page<MovieResponse>> getAvailable(@PageableDefault(size = 20) Pageable pageable) {
        Page<MovieResponse> response = movieService.getAvailable(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    @Operation(summary = "Filmes mais alugados", description = "Lista os filmes mais populares (baseado em aluguéis)")
    @ApiResponse(responseCode = "200", description = "Filmes mais alugados retornados")
    public ResponseEntity<Page<MovieResponse>> getMostRented(@PageableDefault(size = 20) Pageable pageable) {
        Page<MovieResponse> response = movieService.getMostRented(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/external/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar filmes em catálogo externo", description = "Consulta uma API externa para localizar filmes importáveis")
    @ApiResponse(responseCode = "200", description = "Filmes externos retornados")
    public ResponseEntity<List<ExternalMovieResponse>> searchExternal(@RequestParam String title) {
        return ResponseEntity.ok(movieService.searchExternalMovies(title));
    }

    @PostMapping("/external/import/{externalId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Importar filme externo", description = "Importa um filme de uma API externa para o catálogo local")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Filme importado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Filme externo não encontrado")
    })
    public ResponseEntity<MovieResponse> importExternal(@PathVariable String externalId) {
        MovieResponse response = movieService.importExternalMovie(externalId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar filme", description = "Remove um filme do catálogo (apenas admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Filme deletado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Filme não encontrado"),
            @ApiResponse(responseCode = "409", description = "Filme possui locações associadas")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{movieId}/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Adicionar categoria a filme", description = "Associa uma categoria a um filme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria adicionada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Filme ou categoria não encontrado"),
            @ApiResponse(responseCode = "409", description = "Categoria já adicionada")
    })
    public ResponseEntity<MovieResponse> addCategory(@PathVariable Long movieId, @PathVariable Long categoryId) {
        MovieResponse response = movieService.addCategory(movieId, categoryId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{movieId}/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover categoria de filme", description = "Remove uma categoria de um filme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria removida com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Filme ou categoria não encontrado"),
            @ApiResponse(responseCode = "409", description = "Categoria não está associada")
    })
    public ResponseEntity<MovieResponse> removeCategory(@PathVariable Long movieId, @PathVariable Long categoryId) {
        MovieResponse response = movieService.removeCategory(movieId, categoryId);
        return ResponseEntity.ok(response);
    }
}

