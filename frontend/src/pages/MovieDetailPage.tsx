import { useNavigate, useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { fetchMovieById } from "../shared/api/moviesApi";
import { queryKeys } from "../shared/queries/queryKeys";
import { useMovieActions } from "../shared/hooks/useMovieActions";
import { useMoviePoster } from "../shared/hooks/useMoviePoster";
import { formatCurrencyBRL } from "../shared/formatters";
import { Navbar } from "../shared/components/Navbar";

export function MovieDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const movieId = Number(id);
  const actions = useMovieActions();

  const movieQuery = useQuery({
    queryKey: queryKeys.movies.detail(movieId),
    queryFn: () => fetchMovieById(movieId),
    enabled: Number.isFinite(movieId) && movieId > 0
  });

  const movie = movieQuery.data;
  const { posterSrc } = useMoviePoster(movie?.title ?? "", movie?.imageUrl);

  if (movieQuery.isLoading) {
    return (
      <>
        <Navbar />
        <main className="app-shell">
          <p>Carregando filme...</p>
        </main>
      </>
    );
  }

  if (movieQuery.isError || !movie) {
    return (
      <>
        <Navbar />
        <main className="app-shell">
          <section className="page-card">
            <h1>Filme não encontrado</h1>
            <p className="muted">Não foi possível carregar este filme.</p>
            <button type="button" onClick={() => navigate("/")}>
              Voltar ao início
            </button>
          </section>
        </main>
      </>
    );
  }

  const releaseYear = movie.releaseDate ? new Date(movie.releaseDate).getFullYear() : "-";
  const isFavorited = actions.favoriteMovieIds.has(movie.id);
  const hasActiveRental = actions.activeRentalMovieIds.has(movie.id);

  return (
    <>
      <Navbar />
      <main className="detail">
        <div className="detail-backdrop" style={{ backgroundImage: `url(${posterSrc})` }} aria-hidden="true" />
        <div className="detail-overlay" aria-hidden="true" />
        <div className="detail-content app-shell">
          <button type="button" className="button-ghost detail-back" onClick={() => navigate(-1)}>
            ‹ Voltar
          </button>

          <div className="detail-body">
            <img className="detail-poster" src={posterSrc} alt={movie.title} />

            <div className="detail-info">
              <h1>{movie.title}</h1>
              <div className="movie-meta">
                {movie.genre ? <span className="tag tag-info">Gênero: {movie.genre}</span> : null}
                <span className="tag tag-info">Ano: {releaseYear}</span>
                <span className="tag tag-info">Duração: {movie.durationMinutes} min</span>
                <span className="tag tag-info">Tipo: {movie.movieType || "movie"}</span>
                {movie.available ? <span className="tag tag-ok">Disponível</span> : <span className="tag">Indisponível</span>}
              </div>

              {movie.director ? <p className="muted">Diretor: {movie.director}</p> : null}
              {movie.description ? <p className="detail-desc">{movie.description}</p> : null}

              {movie.categories.length > 0 ? (
                <div className="movie-categories">
                  {movie.categories.map((category) => (
                    <span key={category} className="tag">
                      {category}
                    </span>
                  ))}
                </div>
              ) : null}

              <p className="detail-price">
                <strong>Preço da locação:</strong> {formatCurrencyBRL(movie.price)}
              </p>

              <div className="detail-actions">
                {hasActiveRental ? (
                  <button type="button" className="btn-watch" onClick={() => navigate(`/watch/${movie.id}`)}>
                    ▶ Assistir
                  </button>
                ) : (
                  <button
                    type="button"
                    onClick={() => actions.rentMovie(movie.id)}
                    disabled={!movie.available || actions.isRentPending}
                    aria-busy={actions.rentingMovieId === movie.id}
                  >
                    {actions.rentingMovieId === movie.id ? "Alugando..." : "Alugar por 3 dias"}
                  </button>
                )}
                <button
                  type="button"
                  className={isFavorited ? "button-secondary" : "button-ghost"}
                  onClick={() => actions.toggleFavorite(movie.id)}
                  disabled={actions.isFavoritePending}
                  aria-pressed={isFavorited}
                >
                  {isFavorited ? "✓ Nos favoritos" : "+ Favoritar"}
                </button>
              </div>
            </div>
          </div>
        </div>
      </main>
    </>
  );
}
