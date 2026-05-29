import { useQuery } from "@tanstack/react-query";
import { getFavoriteMovies } from "../shared/api/favoritesApi";
import { mapApiError } from "../shared/api/errors";
import { queryKeys } from "../shared/queries/queryKeys";
import { useMovieActions } from "../shared/hooks/useMovieActions";
import { PageLayout } from "../shared/components/PageLayout";
import { MovieCard } from "../shared/components/MovieCard";
import { FavoriteMovieAction } from "../shared/components/FavoriteMovieAction";
import { RentMovieAction } from "../shared/components/RentMovieAction";

export function FavoritesPage() {
  const actions = useMovieActions();

  const favoritesQuery = useQuery({
    queryKey: queryKeys.favorites.list,
    queryFn: getFavoriteMovies,
    placeholderData: (previousData) => previousData,
    staleTime: 60_000
  });

  const favoriteMovies = favoritesQuery.data ?? [];
  const favoritesError = favoritesQuery.error ? mapApiError(favoritesQuery.error, "Nao foi possivel carregar os favoritos.") : null;

  return (
    <PageLayout
      title="Meus favoritos"
      description="Veja os filmes que voce salvou e alugue direto desta lista."
    >
      <section className="card content-stack">
        {favoritesQuery.isLoading ? <p>Carregando favoritos...</p> : null}
        {favoritesError ? <p className="error">{favoritesError}</p> : null}
        {!favoritesQuery.isLoading && favoriteMovies.length === 0 ? <p>Nenhum filme favoritado ainda.</p> : null}

        <section className="list">
          {favoriteMovies.map((movie) => (
            <MovieCard key={movie.id} movie={movie}>
              <FavoriteMovieAction
                isFavorited
                isPending={actions.isFavoritePending}
                onToggle={() => actions.toggleFavorite(movie.id)}
              />
              <RentMovieAction
                movieId={movie.id}
                isAvailable={movie.available}
                isPending={actions.isRentPending}
                isCurrentMoviePending={actions.rentingMovieId === movie.id}
                onRent={(movieId) => actions.rentMovie(movieId)}
              />
            </MovieCard>
          ))}
        </section>
      </section>
    </PageLayout>
  );
}
