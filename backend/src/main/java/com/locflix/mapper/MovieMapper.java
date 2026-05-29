package com.locflix.mapper;

import com.locflix.dto.request.CreateMovieRequest;
import com.locflix.dto.response.MovieResponse;
import com.locflix.entity.Movie;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper para conversão entre Movie entity e seus DTOs.
 */
@Component
public class MovieMapper {

    /**
     * Converte um CreateMovieRequest para uma entidade Movie.
     *
     * @param request Requisição de criação
     * @return Entidade Movie
     */
    public Movie toEntity(CreateMovieRequest request) {
        return Movie.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .genre(request.getGenre())
                .director(request.getDirector())
                .movieType(request.getMovieType())
                .releaseDate(request.getReleaseDate())
                .durationMinutes(request.getDurationMinutes())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .available(request.getAvailable() != null ? request.getAvailable() : true)
                .build();
    }

    /**
     * Converte um CreateMovieRequest para atualizar uma entidade Movie existente.
     *
     * @param request Requisição de atualização
     * @param movie Movie existente a ser atualizado
     * @return Entidade Movie atualizada
     */
    public Movie updateEntity(CreateMovieRequest request, Movie movie) {
        movie.setTitle(request.getTitle().trim());
        movie.setDescription(request.getDescription());
        movie.setGenre(request.getGenre());
        movie.setDirector(request.getDirector());
        movie.setMovieType(request.getMovieType());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setPrice(request.getPrice());
        movie.setImageUrl(request.getImageUrl());
        movie.setAvailable(request.getAvailable() != null ? request.getAvailable() : movie.getAvailable());
        return movie;
    }

    /**
     * Converte uma entidade Movie para MovieResponse.
     *
     * @param movie Entidade Movie
     * @return DTO de resposta
     */
    public MovieResponse toResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .genre(movie.getGenre())
                .director(movie.getDirector())
                .movieType(movie.getMovieType())
                .releaseDate(movie.getReleaseDate())
                .durationMinutes(movie.getDurationMinutes())
                .price(movie.getPrice())
                .imageUrl(movie.getImageUrl())
                .available(movie.getAvailable())
                .categories(movie.getCategories().stream()
                        .map(com.locflix.entity.Category::getName)
                        .collect(Collectors.toSet())
                )
                .createdAt(movie.getCreatedAt())
                .updatedAt(movie.getUpdatedAt())
                .build();
    }
}

