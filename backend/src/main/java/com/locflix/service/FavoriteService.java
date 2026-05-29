package com.locflix.service;

import com.locflix.dto.response.MovieResponse;

import java.util.List;

public interface FavoriteService {
    List<MovieResponse> listFavorites(Long userId);

    MovieResponse addFavorite(Long userId, Long movieId);

    MovieResponse removeFavorite(Long userId, Long movieId);
}

