import { httpClient } from "./httpClient";
import type { MovieResponse, PageResponse } from "../types/api";

export interface MovieListParams {
  page?: number;
  size?: number;
  title?: string;
  onlyAvailable?: boolean;
}

export async function fetchMovies(params: MovieListParams = {}): Promise<PageResponse<MovieResponse>> {
  const page = params.page ?? 0;
  const size = params.size ?? 12;

  if (params.title && params.title.trim().length > 0) {
    const { data } = await httpClient.get<PageResponse<MovieResponse>>("/api/v1/movies/search/title", {
      params: { page, size, title: params.title.trim() }
    });
    return data;
  }

  if (params.onlyAvailable) {
    const { data } = await httpClient.get<PageResponse<MovieResponse>>("/api/v1/movies/available", {
      params: { page, size }
    });
    return data;
  }

  const { data } = await httpClient.get<PageResponse<MovieResponse>>("/api/v1/movies", {
    params: { page, size }
  });
  return data;
}

export async function fetchMovieById(id: number): Promise<MovieResponse> {
  const { data } = await httpClient.get<MovieResponse>(`/api/v1/movies/${id}`);
  return data;
}

export async function fetchPopularMovies(page = 0, size = 12): Promise<PageResponse<MovieResponse>> {
  const { data } = await httpClient.get<PageResponse<MovieResponse>>("/api/v1/movies/popular", {
    params: { page, size }
  });
  return data;
}
