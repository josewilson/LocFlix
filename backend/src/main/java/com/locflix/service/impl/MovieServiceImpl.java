package com.locflix.service.impl;

import com.locflix.dto.request.CreateMovieRequest;
import com.locflix.dto.response.ExternalMovieResponse;
import com.locflix.dto.response.MovieResponse;
import com.locflix.exception.BusinessLogicException;
import com.locflix.exception.ResourceNotFoundException;
import com.locflix.mapper.MovieMapper;
import com.locflix.repository.CategoryRepository;
import com.locflix.repository.MovieRepository;
import com.locflix.service.external.ExternalMovieService;
import com.locflix.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementação do serviço de Filmes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MovieServiceImpl implements MovieService {

    private static final BigDecimal DEFAULT_IMPORTED_PRICE = new BigDecimal("12.90");

    private final MovieRepository movieRepository;
    private final CategoryRepository categoryRepository;
    private final MovieMapper movieMapper;
    private final ExternalMovieService externalMovieService;

    @Override
    public MovieResponse create(CreateMovieRequest request) {
        log.info("Creating new movie: {}", request.getTitle());

        // Verifica se já existe um filme com o mesmo título
        if (movieRepository.existsByTitleIgnoreCase(request.getTitle())) {
            log.warn("Movie already exists: {}", request.getTitle());
            throw new BusinessLogicException("Filme '" + request.getTitle() + "' já existe no catálogo");
        }

        var movie = movieMapper.toEntity(request);
        var savedMovie = movieRepository.save(movie);

        log.info("Movie created successfully with ID: {}", savedMovie.getId());
        return movieMapper.toResponse(savedMovie);
    }

    @Override
    public MovieResponse update(Long id, CreateMovieRequest request) {
        log.info("Updating movie: {}", id);

        var movie = movieRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Filme", "ID", id));

        // Verifica se novo título já existe (se diferente do atual)
        if (!movie.getTitle().equalsIgnoreCase(request.getTitle()) && movieRepository.existsByTitleIgnoreCase(request.getTitle())) {
            log.warn("Movie title already exists: {}", request.getTitle());
            throw new BusinessLogicException("Título '" + request.getTitle() + "' já existe");
        }

        movieMapper.updateEntity(request, movie);
        var updatedMovie = movieRepository.save(movie);

        log.info("Movie updated successfully: {}", id);
        return movieMapper.toResponse(updatedMovie);
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponse getById(Long id) {
        log.debug("Fetching movie by ID: {}", id);

        var movie = movieRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Filme", "ID", id));

        return movieMapper.toResponse(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieResponse> getAll(Pageable pageable) {
        log.debug("Fetching all movies with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());

        var movies = movieRepository.findAll(pageable);
        return movies.map(movieMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieResponse> searchByTitle(String title, Pageable pageable) {
        log.debug("Searching movies by title: {}", title);

        if (title == null || title.isBlank()) {
            return getAll(pageable);
        }

        var movies = movieRepository.findByTitleContainingIgnoreCase(title, pageable);
        return movies.map(movieMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieResponse> searchByGenre(String genre, Pageable pageable) {
        log.debug("Searching movies by genre: {}", genre);

        if (genre == null || genre.isBlank()) {
            return getAll(pageable);
        }

        var movies = movieRepository.findByGenre(genre, pageable);
        return movies.map(movieMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieResponse> getAvailable(Pageable pageable) {
        log.debug("Fetching available movies");

        var movies = movieRepository.findByAvailableTrue(pageable);
        return movies.map(movieMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieResponse> getMostRented(Pageable pageable) {
        log.debug("Fetching most rented movies");

        var movies = movieRepository.findMostRented(pageable);
        return movies.map(movieMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExternalMovieResponse> searchExternalMovies(String title) {
        log.debug("Searching external catalog for title: {}", title);
        return externalMovieService.search(title);
    }

    @Override
    public MovieResponse importExternalMovie(String externalId) {
        log.info("Importing external movie: {}", externalId);

        var externalMovie = externalMovieService.getByExternalId(externalId);

        var existingMovie = movieRepository.findByTitleIgnoreCase(externalMovie.getTitle());
        if (existingMovie.isPresent()) {
            log.info("External movie already imported: {}", externalMovie.getTitle());
            return movieMapper.toResponse(existingMovie.get());
        }

        CreateMovieRequest request = CreateMovieRequest.builder()
                .title(externalMovie.getTitle())
                .description(externalMovie.getDescription())
                .genre(externalMovie.getGenre())
                .director(externalMovie.getDirector())
                .movieType(externalMovie.getMovieType())
                .releaseDate(externalMovie.getReleaseDate() != null ? externalMovie.getReleaseDate() : LocalDate.now())
                .durationMinutes(externalMovie.getDurationMinutes())
                .price(DEFAULT_IMPORTED_PRICE)
                .imageUrl(externalMovie.getImageUrl())
                .available(true)
                .build();

        return create(request);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting movie: {}", id);

        var movie = movieRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Filme", "ID", id));

        if (!movie.getRentals().isEmpty()) {
            log.warn("Cannot delete movie with active rentals: {}", id);
            throw new BusinessLogicException("Não é possível deletar um filme que possui locações associadas");
        }

        movieRepository.delete(movie);
        log.info("Movie deleted successfully: {}", id);
    }

    @Override
    public MovieResponse addCategory(Long movieId, Long categoryId) {
        log.info("Adding category {} to movie {}", categoryId, movieId);

        var movie = movieRepository.findById(movieId)
                .orElseThrow(() -> ResourceNotFoundException.of("Filme", "ID", movieId));

        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> ResourceNotFoundException.of("Categoria", "ID", categoryId));

        if (movie.getCategories().contains(category)) {
            log.warn("Category {} already added to movie {}", categoryId, movieId);
            throw new BusinessLogicException("Categoria já está adicionada ao filme");
        }

        movie.addCategory(category);
        var updatedMovie = movieRepository.save(movie);

        log.info("Category added to movie: {}", movieId);
        return movieMapper.toResponse(updatedMovie);
    }

    @Override
    public MovieResponse removeCategory(Long movieId, Long categoryId) {
        log.info("Removing category {} from movie {}", categoryId, movieId);

        var movie = movieRepository.findById(movieId)
                .orElseThrow(() -> ResourceNotFoundException.of("Filme", "ID", movieId));

        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> ResourceNotFoundException.of("Categoria", "ID", categoryId));

        if (!movie.getCategories().contains(category)) {
            log.warn("Category {} not found in movie {}", categoryId, movieId);
            throw new BusinessLogicException("Categoria não está adicionada ao filme");
        }

        movie.removeCategory(category);
        var updatedMovie = movieRepository.save(movie);

        log.info("Category removed from movie: {}", movieId);
        return movieMapper.toResponse(updatedMovie);
    }
}

