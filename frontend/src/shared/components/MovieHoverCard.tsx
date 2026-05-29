import { useNavigate } from "react-router-dom";
import type { MovieResponse } from "../types/api";
import type { MovieActions } from "../hooks/useMovieActions";

interface MovieHoverCardProps {
  movie: MovieResponse;
  actions: MovieActions;
}

/**
 * Preview exibido ao passar o mouse sobre um pôster (desktop): resumo + ações rápidas.
 */
export function MovieHoverCard({ movie, actions }: MovieHoverCardProps) {
  const navigate = useNavigate();
  const releaseYear = movie.releaseDate ? new Date(movie.releaseDate).getFullYear() : null;
  const isFavorited = actions.favoriteMovieIds.has(movie.id);
  const hasActiveRental = actions.activeRentalMovieIds.has(movie.id);

  return (
    <div className="hover-card" role="group" aria-label={`Ações para ${movie.title}`}>
      <strong className="hover-card-title">{movie.title}</strong>
      <div className="hover-card-meta">
        {hasActiveRental ? <span className="tag tag-ok">Locado</span> : null}
        {movie.available ? <span className="tag tag-ok">Disponível</span> : <span className="tag">Indisponível</span>}
        {releaseYear ? <span className="tag tag-info">{releaseYear}</span> : null}
        {movie.durationMinutes ? <span className="tag tag-info">{movie.durationMinutes} min</span> : null}
      </div>
      {movie.description ? <p className="hover-card-desc">{movie.description}</p> : null}
      <div className="hover-card-actions">
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
            {actions.rentingMovieId === movie.id ? "Alugando..." : "Alugar"}
          </button>
        )}
        <button
          type="button"
          className={isFavorited ? "button-secondary" : "button-ghost"}
          onClick={() => actions.toggleFavorite(movie.id)}
          disabled={actions.isFavoritePending}
          aria-pressed={isFavorited}
        >
          {isFavorited ? "✓ Favorito" : "+ Favoritar"}
        </button>
        <button type="button" className="button-ghost" onClick={() => navigate(`/movie/${movie.id}`)}>
          Detalhes
        </button>
      </div>
    </div>
  );
}
