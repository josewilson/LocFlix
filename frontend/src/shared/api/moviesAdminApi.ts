import { httpClient } from "./httpClient";
import type { CreateMovieRequest, MovieResponse, PageResponse } from "../types/api";

export async function fetchMoviesAdmin(page = 0, size = 20): Promise<PageResponse<MovieResponse>> {
  const { data } = await httpClient.get<PageResponse<MovieResponse>>("/api/v1/movies", {
    params: { page, size }
  });
  return data;
}

export async function createMovie(payload: CreateMovieRequest): Promise<MovieResponse> {
  const { data } = await httpClient.post<MovieResponse>("/api/v1/movies", payload);
  return data;
}

export async function updateMovie(id: number, payload: CreateMovieRequest): Promise<MovieResponse> {
  const { data } = await httpClient.put<MovieResponse>(`/api/v1/movies/${id}`, payload);
  return data;
}

export async function deleteMovie(id: number): Promise<void> {
  await httpClient.delete(`/api/v1/movies/${id}`);
}

export async function addCategoryToMovie(movieId: number, categoryId: number): Promise<MovieResponse> {
  const { data } = await httpClient.post<MovieResponse>(`/api/v1/movies/${movieId}/categories/${categoryId}`);
  return data;
}

export async function removeCategoryFromMovie(movieId: number, categoryId: number): Promise<MovieResponse> {
  const { data } = await httpClient.delete<MovieResponse>(`/api/v1/movies/${movieId}/categories/${categoryId}`);
  return data;
}
