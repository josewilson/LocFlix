import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { fetchMovies, fetchPopularMovies } from "../shared/api/moviesApi";
import { queryKeys } from "../shared/queries/queryKeys";
import { useMovieActions } from "../shared/hooks/useMovieActions";
import { Navbar } from "../shared/components/Navbar";
import { HeroBanner } from "../shared/components/HeroBanner";
import { MovieCarousel } from "../shared/components/MovieCarousel";
import { SkeletonRow } from "../shared/components/SkeletonRow";
import type { MovieResponse } from "../shared/types/api";

interface CarouselRow {
  title: string;
  movies: MovieResponse[];
}

/** Agrupa filmes por categoria; sem categoria, agrupa por gênero. */
function buildCategoryRows(movies: MovieResponse[]): CarouselRow[] {
  const byKey = new Map<string, MovieResponse[]>();

  for (const movie of movies) {
    const keys = movie.categories.length > 0 ? movie.categories : [movie.genre || "Outros"];
    for (const key of keys) {
      const bucket = byKey.get(key) ?? [];
      bucket.push(movie);
      byKey.set(key, bucket);
    }
  }

  return Array.from(byKey.entries())
    .filter(([, list]) => list.length > 0)
    .sort((a, b) => b[1].length - a[1].length)
    .map(([title, list]) => ({ title, movies: list }));
}

export function HomePage() {
  const actions = useMovieActions();

  const popularQuery = useQuery({
    queryKey: queryKeys.movies.popular,
    queryFn: () => fetchPopularMovies(0, 18),
    staleTime: 60_000
  });

  const catalogQuery = useQuery({
    queryKey: queryKeys.movies.list({ page: 0, size: 50, title: "", onlyAvailable: false }),
    queryFn: () => fetchMovies({ page: 0, size: 50 }),
    staleTime: 60_000
  });

  const popularMovies = popularQuery.data?.content ?? [];
  const allMovies = useMemo(() => catalogQuery.data?.content ?? [], [catalogQuery.data]);

  const heroMovie = popularMovies[0] ?? allMovies[0] ?? null;

  const continueWatching = useMemo(
    () => allMovies.filter((movie) => actions.activeRentalMovieIds.has(movie.id)),
    [allMovies, actions.activeRentalMovieIds]
  );

  const favorites = useMemo(
    () => allMovies.filter((movie) => actions.favoriteMovieIds.has(movie.id)),
    [allMovies, actions.favoriteMovieIds]
  );

  const categoryRows = useMemo(() => buildCategoryRows(allMovies), [allMovies]);

  const isLoading = popularQuery.isLoading || catalogQuery.isLoading;

  return (
    <>
      <Navbar />
      <main className="home">
        {isLoading ? (
          <div className="home-rows">
            <SkeletonRow />
            <SkeletonRow />
            <SkeletonRow />
          </div>
        ) : (
          <>
            {heroMovie ? <HeroBanner movie={heroMovie} actions={actions} /> : null}

            <div className="home-rows">
              <MovieCarousel title="Populares na LOCFLIX" movies={popularMovies} actions={actions} />
              <MovieCarousel title="Continue assistindo" movies={continueWatching} actions={actions} />
              <MovieCarousel title="Meus favoritos" movies={favorites} actions={actions} />
              {categoryRows.map((row) => (
                <MovieCarousel key={row.title} title={row.title} movies={row.movies} actions={actions} />
              ))}
            </div>

            {allMovies.length === 0 ? (
              <p className="home-empty">Nenhum filme disponível no catálogo ainda.</p>
            ) : null}
          </>
        )}
      </main>
    </>
  );
}
