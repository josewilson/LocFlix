import { useNavigate } from "react-router-dom";
import type { MovieResponse } from "../types/api";
import type { MovieActions } from "../hooks/useMovieActions";
import { useMoviePoster } from "../hooks/useMoviePoster";
import { MovieHoverCard } from "./MovieHoverCard";

interface MoviePosterProps {
  movie: MovieResponse;
  actions: MovieActions;
}

/**
 * Pôster compacto usado nos carrosséis. Clica para abrir os detalhes e revela
 * um card de ações no hover (desktop).
 */
export function MoviePoster({ movie, actions }: MoviePosterProps) {
  const navigate = useNavigate();
  const { posterSrc, isLoadingImage, handleImageError, handleImageLoad } = useMoviePoster(movie.title, movie.imageUrl);

  return (
    <div className="poster-cell">
      <button
        type="button"
        className="poster-button"
        onClick={() => navigate(`/movie/${movie.id}`)}
        aria-label={`Ver detalhes de ${movie.title}`}
      >
        <img
          src={posterSrc}
          alt={movie.title}
          loading="lazy"
          decoding="async"
          className={isLoadingImage ? "loading" : ""}
          onError={handleImageError}
          onLoad={handleImageLoad}
        />
        {actions.activeRentalMovieIds.has(movie.id) ? <span className="poster-badge">Locado</span> : null}
      </button>
      <MovieHoverCard movie={movie} actions={actions} />
    </div>
  );
}
