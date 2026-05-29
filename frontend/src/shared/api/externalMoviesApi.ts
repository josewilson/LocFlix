import { httpClient } from "./httpClient";
import type { ExternalMovieResponse, MovieResponse } from "../types/api";

export async function searchExternalMovies(title: string): Promise<ExternalMovieResponse[]> {
  const { data } = await httpClient.get<ExternalMovieResponse[]>("/api/v1/movies/external/search", {
    params: { title }
  });
  return data;
}

export async function importExternalMovie(externalId: string): Promise<MovieResponse> {
  const { data } = await httpClient.post<MovieResponse>(`/api/v1/movies/external/import/${externalId}`);
  return data;
}

