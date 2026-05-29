import { useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { addFavoriteMovie, getFavoriteMovies, removeFavoriteMovie } from "../api/favoritesApi";
import { createRental, getActiveRentals } from "../api/rentalsApi";
import { mapApiError } from "../api/errors";
import { queryKeys } from "../queries/queryKeys";
import { useToast } from "../components/ToastProvider";

export interface MovieActions {
  /** IDs de filmes com locação ativa. */
  activeRentalMovieIds: Set<number>;
  /** IDs de filmes favoritados. */
  favoriteMovieIds: Set<number>;
  isFavoritePending: boolean;
  isRentPending: boolean;
  /** ID do filme cuja locação está em andamento (para feedback por item). */
  rentingMovieId: number | null;
  toggleFavorite: (movieId: number) => void;
  rentMovie: (movieId: number) => void;
}

/**
 * Centraliza as ações de favoritar e alugar usadas pelos componentes de catálogo
 * (HomePage, MovieDetailPage, carrosséis), reaproveitando as queries existentes.
 */
export function useMovieActions(): MovieActions {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  const favoritesQuery = useQuery({
    queryKey: queryKeys.favorites.list,
    queryFn: getFavoriteMovies,
    staleTime: 60_000
  });

  const activeRentalsQuery = useQuery({
    queryKey: queryKeys.rentals.active,
    queryFn: getActiveRentals,
    staleTime: 30_000
  });

  const favoriteMovieIds = useMemo(
    () => new Set((favoritesQuery.data ?? []).map((movie) => movie.id)),
    [favoritesQuery.data]
  );

  const activeRentalMovieIds = useMemo(
    () => new Set((activeRentalsQuery.data ?? []).map((rental) => rental.movieId)),
    [activeRentalsQuery.data]
  );

  const toggleFavoriteMutation = useMutation({
    mutationFn: ({ movieId, isFavorite }: { movieId: number; isFavorite: boolean }) =>
      isFavorite ? removeFavoriteMovie(movieId) : addFavoriteMovie(movieId),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: queryKeys.favorites.list });
      showToast("Lista de favoritos atualizada.", "success");
    },
    onError: (error) => {
      showToast(mapApiError(error, "Nao foi possivel atualizar os favoritos."), "error");
    }
  });

  const rentMovieMutation = useMutation({
    mutationFn: (movieId: number) => createRental({ movieId, daysToRent: 3 }),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: queryKeys.rentals.active });
      await queryClient.invalidateQueries({ queryKey: queryKeys.rentals.historyAll });
      showToast("Filme alugado com sucesso.", "success");
      navigate("/rentals");
    },
    onError: (error) => {
      showToast(mapApiError(error, "Nao foi possivel alugar este filme."), "error");
    }
  });

  return {
    activeRentalMovieIds,
    favoriteMovieIds,
    isFavoritePending: toggleFavoriteMutation.isPending,
    isRentPending: rentMovieMutation.isPending,
    rentingMovieId: (rentMovieMutation.variables as number | undefined) ?? null,
    toggleFavorite: (movieId: number) =>
      toggleFavoriteMutation.mutate({ movieId, isFavorite: favoriteMovieIds.has(movieId) }),
    rentMovie: (movieId: number) => rentMovieMutation.mutate(movieId)
  };
}
