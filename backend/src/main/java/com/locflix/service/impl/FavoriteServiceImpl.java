package com.locflix.service.impl;

import com.locflix.dto.response.MovieResponse;
import com.locflix.exception.BusinessLogicException;
import com.locflix.exception.ResourceNotFoundException;
import com.locflix.mapper.MovieMapper;
import com.locflix.repository.MovieRepository;
import com.locflix.repository.UserRepository;
import com.locflix.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> listFavorites(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuário", "ID", userId));

        return user.getFavoriteMovies().stream()
                .map(movieMapper::toResponse)
                .toList();
    }

    @Override
    public MovieResponse addFavorite(Long userId, Long movieId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuário", "ID", userId));
        var movie = movieRepository.findById(movieId)
                .orElseThrow(() -> ResourceNotFoundException.of("Filme", "ID", movieId));

        boolean alreadyFavorite = user.getFavoriteMovies().stream()
                .anyMatch(favoriteMovie -> favoriteMovie.getId().equals(movieId));
        if (alreadyFavorite) {
            throw new BusinessLogicException("Filme já está nos favoritos");
        }

        user.addFavoriteMovie(movie);
        userRepository.save(user);
        log.info("Movie {} added to favorites for user {}", movieId, userId);
        return movieMapper.toResponse(movie);
    }

    @Override
    public MovieResponse removeFavorite(Long userId, Long movieId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuário", "ID", userId));
        var movie = movieRepository.findById(movieId)
                .orElseThrow(() -> ResourceNotFoundException.of("Filme", "ID", movieId));

        boolean favoriteExists = user.getFavoriteMovies().stream()
                .anyMatch(favoriteMovie -> favoriteMovie.getId().equals(movieId));
        if (!favoriteExists) {
            throw new BusinessLogicException("Filme não está nos favoritos");
        }

        user.removeFavoriteMovie(movie);
        userRepository.save(user);
        log.info("Movie {} removed from favorites for user {}", movieId, userId);
        return movieMapper.toResponse(movie);
    }
}

