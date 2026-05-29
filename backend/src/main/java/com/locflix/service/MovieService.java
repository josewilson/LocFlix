package com.locflix.service;

import com.locflix.dto.request.CreateMovieRequest;
import com.locflix.dto.response.ExternalMovieResponse;
import com.locflix.dto.response.MovieResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface de serviço para gerenciamento de Filmes.
 */
public interface MovieService {

    /**
     * Cria um novo filme.
     *
     * @param request Dados do filme
     * @return Filme criado
     */
    MovieResponse create(CreateMovieRequest request);

    /**
     * Atualiza um filme existente.
     *
     * @param id ID do filme
     * @param request Dados atualizados
     * @return Filme atualizado
     */
    MovieResponse update(Long id, CreateMovieRequest request);

    /**
     * Obtém um filme por ID.
     *
     * @param id ID do filme
     * @return Filme encontrado
     */
    MovieResponse getById(Long id);

    /**
     * Lista todos os filmes com paginação.
     *
     * @param pageable Informações de paginação
     * @return Página de filmes
     */
    Page<MovieResponse> getAll(Pageable pageable);

    /**
     * Busca filmes por título.
     *
     * @param title Título a buscar
     * @param pageable Informações de paginação
     * @return Página de filmes encontrados
     */
    Page<MovieResponse> searchByTitle(String title, Pageable pageable);

    /**
     * Busca filmes por gênero.
     *
     * @param genre Gênero a buscar
     * @param pageable Informações de paginação
     * @return Página de filmes do gênero
     */
    Page<MovieResponse> searchByGenre(String genre, Pageable pageable);

    /**
     * Lista filmes disponíveis.
     *
     * @param pageable Informações de paginação
     * @return Página de filmes disponíveis
     */
    Page<MovieResponse> getAvailable(Pageable pageable);

    /**
     * Busca filmes mais alugados.
     *
     * @param pageable Informações de paginação
     * @return Página de filmes mais populares
     */
    Page<MovieResponse> getMostRented(Pageable pageable);

    /**
     * Busca filmes em uma API externa.
     */
    List<ExternalMovieResponse> searchExternalMovies(String title);

    /**
     * Importa um filme externo para o catálogo local.
     */
    MovieResponse importExternalMovie(String externalId);

    /**
     * Deleta um filme.
     *
     * @param id ID do filme a deletar
     */
    void delete(Long id);

    /**
     * Adiciona uma categoria a um filme.
     *
     * @param movieId ID do filme
     * @param categoryId ID da categoria
     * @return Filme atualizado
     */
    MovieResponse addCategory(Long movieId, Long categoryId);

    /**
     * Remove uma categoria de um filme.
     *
     * @param movieId ID do filme
     * @param categoryId ID da categoria
     * @return Filme atualizado
     */
    MovieResponse removeCategory(Long movieId, Long categoryId);
}

