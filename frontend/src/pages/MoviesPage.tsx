import { useEffect, useMemo, useState } from "react";
import type { FormEvent } from "react";
import { useSearchParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { fetchMovies } from "../shared/api/moviesApi";
import { queryKeys } from "../shared/queries/queryKeys";
import { useMovieActions } from "../shared/hooks/useMovieActions";
import { PageLayout } from "../shared/components/PageLayout";
import { MovieCard } from "../shared/components/MovieCard";
import { FavoriteMovieAction } from "../shared/components/FavoriteMovieAction";
import { RentMovieAction } from "../shared/components/RentMovieAction";
import { WatchMovieAction } from "../shared/components/WatchMovieAction";

export function MoviesPage() {
  const actions = useMovieActions();
  const [searchParams] = useSearchParams();
  const titleParam = searchParams.get("title") ?? "";

  const [page, setPage] = useState(0);
  const [searchInput, setSearchInput] = useState(titleParam);
  const [searchTerm, setSearchTerm] = useState(titleParam);
  const [onlyAvailable, setOnlyAvailable] = useState(true);

  // Sincroniza a busca vinda da Navbar (?title=) com o estado local.
  useEffect(() => {
    setSearchInput(titleParam);
    setSearchTerm(titleParam);
    setPage(0);
  }, [titleParam]);

  const { data, isLoading } = useQuery({
    queryKey: queryKeys.movies.list({ page, size: 8, title: searchTerm, onlyAvailable }),
    queryFn: () => fetchMovies({ page, size: 8, title: searchTerm, onlyAvailable }),
    throwOnError: false,
    placeholderData: (previousData) => previousData
  });

  const movies = data?.content ?? [];
  const totalPages = Math.max(data?.totalPages ?? 1, 1);
  const totalResults = data?.totalElements ?? movies.length;
  const activeFilters = useMemo(
    () => [searchTerm ? `Titulo: "${searchTerm}"` : null, onlyAvailable ? "Somente disponiveis" : "Todos os filmes"].filter(Boolean),
    [searchTerm, onlyAvailable]
  );
  const hasActiveFilters = activeFilters.length > 0;

  function handleSearchSubmit(event: FormEvent<HTMLFormElement>): void {
    event.preventDefault();
    setPage(0);
    setSearchTerm(searchInput.trim());
  }

  function handleClearFilters(): void {
    setSearchInput("");
    setSearchTerm("");
    setOnlyAvailable(true);
    setPage(0);
  }

  return (
    <PageLayout
      title="Catalogo de filmes"
      description="Explore, filtre e alugue filmes com uma experiencia mais fluida e profissional."
    >
      <section className="card content-stack">
        <form className="toolbar" onSubmit={handleSearchSubmit}>
          <input
            placeholder="Buscar por titulo"
            value={searchInput}
            onChange={(event) => setSearchInput(event.target.value)}
          />
          <button type="submit">Buscar</button>
          <button type="button" onClick={handleClearFilters}>
            Limpar filtros
          </button>
          <label className="checkbox-row">
            <input
              type="checkbox"
              checked={onlyAvailable}
              onChange={(event) => {
                setOnlyAvailable(event.target.checked);
                setPage(0);
              }}
            />
            Somente disponiveis
          </label>
        </form>

        <div className="summary-row" aria-live="polite">
          <div>
            <p className="eyebrow">Resultados</p>
            <strong>
              {totalResults === 0
                ? "Nenhum filme encontrado"
                : `${totalResults} filme${totalResults === 1 ? "" : "s"} encontrado${totalResults === 1 ? "" : "s"}`}
            </strong>
            <p className="muted">
              {hasActiveFilters ? `Filtros ativos: ${activeFilters.join(" • ")}` : "Nenhum filtro aplicado."}
            </p>
          </div>
          <div className="summary-stats">
            <span className="tag tag-info">
              Pagina {page + 1} de {totalPages}
            </span>
            <span className="tag tag-info">{movies.length} nesta pagina</span>
          </div>
        </div>

        {isLoading ? <p>Carregando catalogo...</p> : null}
        {!isLoading && movies.length === 0 ? <p>Nenhum filme encontrado para esse filtro.</p> : null}

        <section className="list">
          {movies.map((movie) => (
            <MovieCard key={movie.id} movie={movie}>
              <WatchMovieAction movieId={movie.id} hasActiveRental={actions.activeRentalMovieIds.has(movie.id)} />
              <FavoriteMovieAction
                isFavorited={actions.favoriteMovieIds.has(movie.id)}
                isPending={actions.isFavoritePending}
                onToggle={() => actions.toggleFavorite(movie.id)}
              />
              <RentMovieAction
                movieId={movie.id}
                isAvailable={movie.available}
                isPending={actions.isRentPending}
                isCurrentMoviePending={actions.rentingMovieId === movie.id}
                onRent={(movieId) => actions.rentMovie(movieId)}
                showProcessingText
              />
            </MovieCard>
          ))}
        </section>

        <div className="pagination">
          <button type="button" disabled={page === 0} onClick={() => setPage((old) => Math.max(old - 1, 0))}>
            Anterior
          </button>
          <span>
            Pagina {page + 1} de {totalPages}
          </span>
          <button type="button" disabled={page + 1 >= totalPages} onClick={() => setPage((old) => old + 1)}>
            Proxima
          </button>
        </div>
      </section>
    </PageLayout>
  );
}
