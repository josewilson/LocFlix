package com.locflix.repository;

import com.locflix.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * Repository para acesso aos dados de Filmes no banco de dados.
 */
@SuppressWarnings("unused")
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Override
    @EntityGraph(attributePaths = {"categories"})
    @NonNull
    Page<Movie> findAll(@NonNull Pageable pageable);

    /**
     * Busca filmes por título (com paginação).
     *
     * @param title Título parcial a buscar
     * @param pageable Informações de paginação
     * @return Página de filmes encontrados
     */
    @EntityGraph(attributePaths = {"categories"})
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * Busca filmes por gênero (com paginação).
     *
     * @param genre Gênero a buscar
     * @param pageable Informações de paginação
     * @return Página de filmes do gênero
     */
    @EntityGraph(attributePaths = {"categories"})
    Page<Movie> findByGenre(String genre, Pageable pageable);

    /**
     * Busca filmes disponíveis (com paginação).
     *
     * @param pageable Informações de paginação
     * @return Página de filmes disponíveis
     */
    @EntityGraph(attributePaths = {"categories"})
    Page<Movie> findByAvailableTrue(Pageable pageable);

    /**
     * Busca filmes mais alugados (com paginação).
     *
     * @param pageable Informações de paginação
     * @return Página de filmes ordenados por número de aluguéis
     */
    @Query(
            value = """
                    SELECT m FROM Movie m
                    ORDER BY (SELECT COUNT(r) FROM Rental r WHERE r.movie = m) DESC, m.title
                    """,
            countQuery = "SELECT COUNT(m) FROM Movie m"
    )
    @EntityGraph(attributePaths = {"categories"})
    Page<Movie> findMostRented(Pageable pageable);


    boolean existsByTitleIgnoreCase(String title);

    Optional<Movie> findByTitleIgnoreCase(String title);
}

