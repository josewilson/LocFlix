import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { MovieCard } from "./MovieCard";
import type { MovieResponse } from "../types/api";

const baseMovie: MovieResponse = {
  id: 1,
  title: "Inception",
  description: "Um ladrão entra nos sonhos para roubar segredos.",
  genre: "Sci-Fi",
  director: "Christopher Nolan",
  movieType: "movie",
  releaseDate: "2010-07-16",
  durationMinutes: 148,
  price: 12.9,
  available: true,
  categories: [],
  createdAt: "2026-04-21T00:00:00Z",
  updatedAt: "2026-04-21T00:00:00Z"
};

describe("MovieCard", () => {
  it("exibe uma capa mesmo quando o filme não possui imageUrl", () => {
    render(<MovieCard movie={baseMovie} />);

    const poster = screen.getByRole("img", { name: /inception/i });
    expect(poster).toBeInTheDocument();
    expect(poster).toHaveAttribute("src", expect.stringContaining("data:image/svg+xml"));
  });

  it("usa a imagem do filme quando imageUrl está disponível", () => {
    render(<MovieCard movie={{ ...baseMovie, imageUrl: "https://example.com/poster.jpg" }} />);

    const poster = screen.getByRole("img", { name: /inception/i });
    expect(poster).toHaveAttribute("src", "https://example.com/poster.jpg");
  });
});

