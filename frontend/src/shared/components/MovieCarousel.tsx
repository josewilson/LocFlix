import { useRef } from "react";
import type { MovieResponse } from "../types/api";
import type { MovieActions } from "../hooks/useMovieActions";
import { MoviePoster } from "./MoviePoster";

interface MovieCarouselProps {
  title: string;
  movies: MovieResponse[];
  actions: MovieActions;
}

/**
 * Fileira horizontal de pôsteres com scroll lateral e botões de navegação.
 * Não renderiza nada quando não há filmes.
 */
export function MovieCarousel({ title, movies, actions }: MovieCarouselProps) {
  const trackRef = useRef<HTMLDivElement>(null);

  if (movies.length === 0) {
    return null;
  }

  function scrollByAmount(direction: 1 | -1): void {
    const track = trackRef.current;
    if (!track) return;
    track.scrollBy({ left: direction * track.clientWidth * 0.85, behavior: "smooth" });
  }

  return (
    <section className="carousel">
      <h2 className="carousel-title">{title}</h2>
      <div className="carousel-viewport">
        <button
          type="button"
          className="carousel-arrow carousel-arrow-left"
          onClick={() => scrollByAmount(-1)}
          aria-label="Anterior"
        >
          ‹
        </button>
        <div className="carousel-track" ref={trackRef}>
          {movies.map((movie) => (
            <MoviePoster key={movie.id} movie={movie} actions={actions} />
          ))}
        </div>
        <button
          type="button"
          className="carousel-arrow carousel-arrow-right"
          onClick={() => scrollByAmount(1)}
          aria-label="Próximo"
        >
          ›
        </button>
      </div>
    </section>
  );
}
