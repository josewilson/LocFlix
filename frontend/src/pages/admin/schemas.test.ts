import { describe, expect, it } from "vitest";
import { categoryFormSchema, movieFormSchema } from "./schemas";

describe("admin schemas", () => {
  it("valida payload de filme com dados corretos", () => {
    const result = movieFormSchema.safeParse({
      title: "Inception",
      description: "Filme de ficcao cientifica",
      genre: "Sci-Fi",
      releaseDate: "2010-07-16",
      durationMinutes: 148,
      price: 14.99,
      imageUrl: "https://example.com/inception.jpg",
      available: true
    });

    expect(result.success).toBe(true);
  });

  it("rejeita filme com preco invalido", () => {
    const result = movieFormSchema.safeParse({
      title: "Filme",
      durationMinutes: 120,
      price: 0,
      available: true
    });

    expect(result.success).toBe(false);
  });

  it("valida categoria com nome obrigatorio", () => {
    const result = categoryFormSchema.safeParse({
      name: "Acao",
      description: "Filmes de acao"
    });

    expect(result.success).toBe(true);
  });

  it("rejeita categoria sem nome", () => {
    const result = categoryFormSchema.safeParse({
      name: "",
      description: "Categoria invalida"
    });

    expect(result.success).toBe(false);
  });
});

