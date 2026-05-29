import axios from "axios";
import type { ExternalMovieResponse } from "../types/api";

const API_BASE = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

/**
 * Serviço para buscar imagens de filmes da API externa (OMDB)
 */
export async function searchExternalMovie(
  title: string
): Promise<ExternalMovieResponse | null> {
  try {
    const response = await axios.get<ExternalMovieResponse[]>(
      `${API_BASE}/movies/external/search`,
      {
        params: { title },
        timeout: 5000,
      }
    );

    if (response.data && response.data.length > 0) {
      // Retorna o primeiro resultado (mais relevante)
      const exactMatch = response.data.find(
        (m) => m.title.toLowerCase() === title.toLowerCase()
      );
      return exactMatch || response.data[0];
    }

    return null;
  } catch (error) {
    console.warn(`Erro ao buscar filme externo "${title}":`, error);
    return null;
  }
}

/**
 * Busca a URL da imagem de um filme, tentando primeiro a API externa
 */
export async function getMovieImageUrl(
  title: string,
  currentImageUrl?: string
): Promise<string | null> {
  // Se já tem imageUrl, retorna direto
  if (currentImageUrl) {
    return currentImageUrl;
  }

  try {
    const externalMovie = await searchExternalMovie(title);
    if (externalMovie?.imageUrl) {
      return externalMovie.imageUrl;
    }
  } catch (error) {
    console.warn("Erro ao buscar imagem da API externa:", error);
  }

  return null;
}

