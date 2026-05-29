package com.locflix.service.external;

import com.locflix.dto.response.ExternalMovieResponse;

import java.util.List;

public interface ExternalMovieService {
    List<ExternalMovieResponse> search(String title);

    ExternalMovieResponse getByExternalId(String externalId);
}

