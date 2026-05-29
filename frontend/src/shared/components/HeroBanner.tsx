import { useNavigate } from "react-router-dom";
import type { MovieResponse } from "../types/api";
import type { MovieActions } from "../hooks/useMovieActions";
import { useMoviePoster } from "../hooks/useMoviePoster";

interface HeroBannerProps {
  movie: MovieResponse;
  actions: MovieActions;
}

/**
 * Destaque em tela cheia no topo da Home, no estilo Netflix.
 */
export function HeroBanner({ movie, actions }: HeroBannerProps) {
  const navigate = useNavigate();
  const { posterSrc } = useMoviePoster(movie.title, movie.imageUrl);
  const hasActiveRental = actions.activeRentalMovieIds.has(movie.id);

  return (
    <section className="hero" aria-label={`Destaque: ${movie.title}`}>
      <div className="hero-backdrop" style={{ backgroundImage: `url(${posterSrc})` }} aria-hidden="true" />
      <div className="hero-overlay" aria-hidden="true" />
      <div className="hero-content">
        <p className="eyebrow">Em destaque</p>
        <h1 className="hero-title">{movie.title}</h1>
        <div className="hero-meta">
          {movie.genre ? <span className="tag tag-info">{movie.genre}</span> : null}
          {movie.releaseDate ? (
            <span className="tag tag-info">{new Date(movie.releaseDate).getFullYear()}</span>
          ) : null}
          {movie.durationMinutes ? <span className="tag tag-info">{movie.durationMinutes} min</span> : null}
        </div>
        {movie.description ? <p className="hero-desc">{movie.description}</p> : null}
        <div className="hero-actions">
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
              {actions.rentingMovieId === movie.id ? "Alugando..." : "▶ Alugar e assistir"}
            </button>
          )}
          <button type="button" className="button-secondary" onClick={() => navigate(`/movie/${movie.id}`)}>
            ⓘ Mais informações
          </button>
        </div>
      </div>
    </section>
  );
}
