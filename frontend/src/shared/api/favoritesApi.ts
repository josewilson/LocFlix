import { httpClient } from "./httpClient";
import type { MovieResponse } from "../types/api";

export async function getFavoriteMovies(): Promise<MovieResponse[]> {
  const { data } = await httpClient.get<MovieResponse[]>("/api/v1/favorites");
  return data;
}

export async function addFavoriteMovie(movieId: number): Promise<MovieResponse> {
  const { data } = await httpClient.post<MovieResponse>(`/api/v1/favorites/${movieId}`);
  return data;
}

export async function removeFavoriteMovie(movieId: number): Promise<MovieResponse> {
  const { data } = await httpClient.delete<MovieResponse>(`/api/v1/favorites/${movieId}`);
  return data;
}

