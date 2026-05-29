import { type ReactNode } from "react";
import type { MovieResponse } from "../types/api";
import { formatCurrencyBRL } from "../formatters";
import { useMoviePoster } from "../hooks/useMoviePoster";

interface MovieCardProps {
  movie: MovieResponse;
  children?: ReactNode;
  showPrice?: boolean;
  showCategories?: boolean;
}

export function MovieCard({ movie, children, showPrice = true, showCategories = true }: MovieCardProps) {
  const releaseYear = movie.releaseDate ? new Date(movie.releaseDate).getFullYear() : "-";
  const { posterSrc, isLoadingImage, handleImageError, handleImageLoad } = useMoviePoster(movie.title, movie.imageUrl);

  return (
    <article className="card">
      <div className="movie-poster">
        <img
          src={posterSrc}
          alt={movie.title}
          loading="lazy"
          decoding="async"
          className={isLoadingImage ? "loading" : ""}
          onError={handleImageError}
          onLoad={handleImageLoad}
        />
      </div>
      <h2>{movie.title}</h2>
      <p>{movie.description}</p>
      <div className="movie-meta">
        <span className="tag tag-info">Genero: {movie.genre || "-"}</span>
        <span className="tag tag-info">Tipo: {movie.movieType || "movie"}</span>
        <span className="tag tag-info">Ano: {releaseYear}</span>
        <span className="tag tag-info">Duracao: {movie.durationMinutes} min</span>
      </div>
      <p className="muted">Diretor: {movie.director || "-"}</p>
      {showCategories && movie.categories.length > 0 ? (
        <div className="movie-categories">
          {movie.categories.map((category) => (
            <span key={category} className="tag">
              {category}
            </span>
          ))}
        </div>
      ) : null}
      {showPrice ? (
        <p>
          <strong>Preco:</strong> {formatCurrencyBRL(movie.price)}
        </p>
      ) : null}
      <div className="actions">
        {children}
        {!movie.available ? <span className="tag">Indisponivel</span> : <span className="tag tag-ok">Disponivel</span>}
      </div>
    </article>
  );
}

