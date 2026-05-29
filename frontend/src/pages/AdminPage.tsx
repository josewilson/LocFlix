import { useState } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { createCategory, deleteCategory, fetchCategories, updateCategory } from "../shared/api/categoriesApi";
import { mapApiError } from "../shared/api/errors";
import {
  addCategoryToMovie,
  createMovie,
  deleteMovie,
  fetchMoviesAdmin,
  removeCategoryFromMovie,
  updateMovie
} from "../shared/api/moviesAdminApi";
import { importExternalMovie, searchExternalMovies } from "../shared/api/externalMoviesApi";
import { getOverdueRentals } from "../shared/api/rentalsApi";
import type { CreateCategoryRequest, CreateMovieRequest, ExternalMovieResponse } from "../shared/types/api";
import { queryKeys } from "../shared/queries/queryKeys";
import { categoryFormSchema, movieFormSchema, type CategoryFormValues, type MovieFormValues } from "./admin/schemas";
import { useToast } from "../shared/components/ToastProvider";
import { PageLayout } from "../shared/components/PageLayout";
import { MovieCard } from "../shared/components/MovieCard";

const overdueRentalsQueryKey = ["rentals", "overdue"] as const;
const categoriesListQueryKey = ["categories", "list"] as const;

type AdminTab = "overdue" | "movies" | "categories" | "external";

const movieDefaultValues: MovieFormValues = {
  title: "",
  description: "",
  genre: "",
  director: "",
  movieType: "movie",
  releaseDate: "",
  durationMinutes: 120,
  price: 9.9,
  imageUrl: "",
  available: true
};

const categoryDefaultValues: CategoryFormValues = {
  name: "",
  description: ""
};

export function AdminPage() {
  const queryClient = useQueryClient();
  const { showToast } = useToast();
  const [activeTab, setActiveTab] = useState<AdminTab>("overdue");
  const [editingMovieId, setEditingMovieId] = useState<number | null>(null);
  const [editingCategoryId, setEditingCategoryId] = useState<number | null>(null);
  const [selectedCategoryByMovie, setSelectedCategoryByMovie] = useState<Record<number, number>>({});
  const [externalSearchInput, setExternalSearchInput] = useState("");
  const [externalSearchTerm, setExternalSearchTerm] = useState("");
  const [error, setError] = useState<string | null>(null);

  const movieForm = useForm<MovieFormValues>({
    resolver: zodResolver(movieFormSchema),
    defaultValues: movieDefaultValues
  });

  const categoryForm = useForm<CategoryFormValues>({
    resolver: zodResolver(categoryFormSchema),
    defaultValues: categoryDefaultValues
  });

  const overdueQuery = useQuery({
    queryKey: queryKeys.rentals.overdue(0, 20),
    queryFn: () => getOverdueRentals(0, 20),
    staleTime: 30_000,
    placeholderData: (previousData) => previousData
  });

  const moviesQuery = useQuery({
    queryKey: queryKeys.movies.adminList(0, 20),
    queryFn: () => fetchMoviesAdmin(0, 20),
    staleTime: 30_000,
    placeholderData: (previousData) => previousData
  });

  const categoriesQuery = useQuery({
    queryKey: queryKeys.categories.list(0, 50),
    queryFn: () => fetchCategories(0, 50),
    staleTime: 60_000,
    placeholderData: (previousData) => previousData
  });

  const externalMoviesQuery = useQuery({
    queryKey: queryKeys.movies.externalSearch(externalSearchTerm),
    queryFn: () => searchExternalMovies(externalSearchTerm),
    enabled: activeTab === "external" && externalSearchTerm.trim().length > 0,
    placeholderData: (previousData) => previousData
  });

  const refreshAdminData = async (): Promise<void> => {
    await queryClient.invalidateQueries({ queryKey: queryKeys.movies.all });
    await queryClient.invalidateQueries({ queryKey: categoriesListQueryKey });
    await queryClient.invalidateQueries({ queryKey: overdueRentalsQueryKey });
  };

  const saveMovieMutation = useMutation({
    mutationFn: ({ id, payload }: { id: number | null; payload: CreateMovieRequest }) =>
      id ? updateMovie(id, payload) : createMovie(payload),
    onSuccess: async () => {
      setError(null);
      movieForm.reset(movieDefaultValues);
      setEditingMovieId(null);
      showToast(editingMovieId ? "Filme atualizado com sucesso." : "Filme criado com sucesso.", "success");
      await refreshAdminData();
    },
    onError: (requestError) => {
      const message = mapApiError(requestError, "Nao foi possivel salvar o filme.");
      setError(message);
      showToast(message, "error");
    }
  });

  const saveCategoryMutation = useMutation({
    mutationFn: ({ id, payload }: { id: number | null; payload: CreateCategoryRequest }) =>
      id ? updateCategory(id, payload) : createCategory(payload),
    onSuccess: async () => {
      setError(null);
      categoryForm.reset(categoryDefaultValues);
      setEditingCategoryId(null);
      showToast(editingCategoryId ? "Categoria atualizada com sucesso." : "Categoria criada com sucesso.", "success");
      await refreshAdminData();
    },
    onError: (requestError) => {
      const message = mapApiError(requestError, "Nao foi possivel salvar a categoria.");
      setError(message);
      showToast(message, "error");
    }
  });

  const importExternalMutation = useMutation({
    mutationFn: (externalId: string) => importExternalMovie(externalId),
    onSuccess: async () => {
      setError(null);
      showToast("Filme externo importado com sucesso.", "success");
      await refreshAdminData();
    },
    onError: (requestError) => {
      const message = mapApiError(requestError, "Nao foi possivel importar o filme externo.");
      setError(message);
      showToast(message, "error");
    }
  });

  const deleteMovieMutation = useMutation({
    mutationFn: (id: number) => deleteMovie(id),
    onSuccess: async () => {
      setError(null);
      showToast("Filme removido com sucesso.", "success");
      await refreshAdminData();
    },
    onError: (requestError) => {
      const message = mapApiError(requestError, "Nao foi possivel remover o filme.");
      setError(message);
      showToast(message, "error");
    }
  });

  const deleteCategoryMutation = useMutation({
    mutationFn: (id: number) => deleteCategory(id),
    onSuccess: async () => {
      setError(null);
      showToast("Categoria removida com sucesso.", "success");
      await refreshAdminData();
    },
    onError: (requestError) => {
      const message = mapApiError(requestError, "Nao foi possivel remover a categoria.");
      setError(message);
      showToast(message, "error");
    }
  });

  const addCategoryMutation = useMutation({
    mutationFn: ({ movieId, categoryId }: { movieId: number; categoryId: number }) => addCategoryToMovie(movieId, categoryId),
    onSuccess: async () => {
      setError(null);
      showToast("Categoria associada ao filme.", "success");
      await refreshAdminData();
    },
    onError: (requestError) => {
      const message = mapApiError(requestError, "Nao foi possivel associar categoria ao filme.");
      setError(message);
      showToast(message, "error");
    }
  });

  const removeCategoryMutation = useMutation({
    mutationFn: ({ movieId, categoryId }: { movieId: number; categoryId: number }) => removeCategoryFromMovie(movieId, categoryId),
    onSuccess: async () => {
      setError(null);
      showToast("Categoria removida do filme.", "success");
      await refreshAdminData();
    },
    onError: (requestError) => {
      const message = mapApiError(requestError, "Nao foi possivel remover categoria do filme.");
      setError(message);
      showToast(message, "error");
    }
  });

  const overdueRentals = overdueQuery.data?.content ?? [];
  const movies = moviesQuery.data?.content ?? [];
  const categories = categoriesQuery.data?.content ?? [];
  const externalMovies: ExternalMovieResponse[] = externalMoviesQuery.data ?? [];

  const isLoading = overdueQuery.isLoading || moviesQuery.isLoading || categoriesQuery.isLoading;

  const getCategoryIdByName = (name: string): number | null => {
    const category = categories.find((item) => item.name === name);
    return category ? category.id : null;
  };

  function normalizeMoviePayload(values: MovieFormValues): CreateMovieRequest {
    return {
      title: values.title.trim(),
      description: values.description?.trim() || undefined,
      genre: values.genre?.trim() || undefined,
      director: values.director?.trim() || undefined,
      movieType: values.movieType?.trim() || undefined,
      releaseDate: values.releaseDate?.trim() || undefined,
      durationMinutes: values.durationMinutes,
      price: values.price,
      imageUrl: values.imageUrl?.trim() || undefined,
      available: values.available
    };
  }

  function normalizeCategoryPayload(values: CategoryFormValues): CreateCategoryRequest {
    return {
      name: values.name.trim(),
      description: values.description?.trim() || undefined
    };
  }

  return (
    <PageLayout
      title="Admin"
      description="Gerencie filmes, categorias e locacoes atrasadas com uma interface focada em operacao."
    >
      <section className="card content-stack">

        <div className="actions">
          <button type="button" className={activeTab === "overdue" ? "button-secondary" : "button-ghost"} onClick={() => setActiveTab("overdue")}>
            Atrasos
          </button>
          <button type="button" className={activeTab === "movies" ? "button-secondary" : "button-ghost"} onClick={() => setActiveTab("movies")}>
            Filmes
          </button>
          <button type="button" className={activeTab === "categories" ? "button-secondary" : "button-ghost"} onClick={() => setActiveTab("categories")}>
            Categorias
          </button>
          <button type="button" className={activeTab === "external" ? "button-secondary" : "button-ghost"} onClick={() => setActiveTab("external")}>
            Catálogo externo
          </button>
        </div>

        {isLoading ? <p>Carregando painel administrativo...</p> : null}
        {error ? <p className="error">{error}</p> : null}

        {!isLoading && activeTab === "overdue" ? (
          <section className="list">
            {overdueRentals.length === 0 ? <p>Nenhuma locacao em atraso no momento.</p> : null}
            {overdueRentals.map((rental) => (
              <article key={rental.id} className="card">
                <h2>{rental.movieTitle}</h2>
                <p>
                  Cliente: <strong>{rental.userName}</strong>
                </p>
                <p>
                  Vencimento: <strong>{new Date(rental.dueDate).toLocaleDateString("pt-BR")}</strong>
                </p>
              </article>
            ))}
          </section>
        ) : null}

        {!isLoading && activeTab === "movies" ? (
          <>
            <form
              className="form-grid card"
              onSubmit={movieForm.handleSubmit((values) => {
                setError(null);
                saveMovieMutation.mutate({
                  id: editingMovieId,
                  payload: normalizeMoviePayload(values)
                });
              })}
            >
              <h2>{editingMovieId ? "Editar filme" : "Novo filme"}</h2>
              <input placeholder="Titulo" {...movieForm.register("title")} />
              {movieForm.formState.errors.title ? <p className="error">{movieForm.formState.errors.title.message}</p> : null}

              <input placeholder="Genero" {...movieForm.register("genre")} />
              <input placeholder="Diretor" {...movieForm.register("director")} />
              {movieForm.formState.errors.director ? <p className="error">{movieForm.formState.errors.director.message}</p> : null}
              <input placeholder="Tipo (movie, series, etc.)" {...movieForm.register("movieType")} />
              {movieForm.formState.errors.movieType ? <p className="error">{movieForm.formState.errors.movieType.message}</p> : null}
              <input placeholder="Data de lancamento (YYYY-MM-DD)" {...movieForm.register("releaseDate")} />
              {movieForm.formState.errors.releaseDate ? <p className="error">{movieForm.formState.errors.releaseDate.message}</p> : null}

              <input type="number" placeholder="Duracao em minutos" {...movieForm.register("durationMinutes")} />
              {movieForm.formState.errors.durationMinutes ? <p className="error">{movieForm.formState.errors.durationMinutes.message}</p> : null}

              <input type="number" step="0.01" min="0.01" placeholder="Preco" {...movieForm.register("price")} />
              {movieForm.formState.errors.price ? <p className="error">{movieForm.formState.errors.price.message}</p> : null}

              <input placeholder="URL da imagem" {...movieForm.register("imageUrl")} />
              {movieForm.formState.errors.imageUrl ? <p className="error">{movieForm.formState.errors.imageUrl.message}</p> : null}

              <label className="checkbox-row">
                <input type="checkbox" {...movieForm.register("available")} />
                Disponivel
              </label>

              <input placeholder="Descricao" {...movieForm.register("description")} />

              <div className="actions">
                <button type="submit" disabled={saveMovieMutation.isPending}>
                  {saveMovieMutation.isPending ? "Salvando..." : editingMovieId ? "Atualizar" : "Criar"}
                </button>
                {editingMovieId ? (
                  <button
                    className="button-ghost"
                    type="button"
                    onClick={() => {
                      movieForm.reset(movieDefaultValues);
                      setEditingMovieId(null);
                    }}
                  >
                    Cancelar edicao
                  </button>
                ) : null}
              </div>
            </form>

            <section className="list">
              {movies.map((movie) => (
                <MovieCard key={movie.id} movie={movie} showCategories={false}>
                  <>
                    {(movie.categories ?? []).map((categoryName) => {
                      const categoryId = getCategoryIdByName(categoryName);
                      return (
                        <span className="tag tag-ok" key={`${movie.id}-${categoryName}`}>
                          {categoryName}
                          {categoryId ? (
                            <button
                              className="link-button"
                              type="button"
                              onClick={() => removeCategoryMutation.mutate({ movieId: movie.id, categoryId })}
                            >
                              x
                            </button>
                          ) : null}
                        </span>
                      );
                    })}
                  </>

                  <>
                    <select
                      value={selectedCategoryByMovie[movie.id] ?? ""}
                      onChange={(event) =>
                        setSelectedCategoryByMovie((old) => ({
                          ...old,
                          [movie.id]: Number(event.target.value)
                        }))
                      }
                    >
                      <option value="">Selecionar categoria</option>
                      {categories.map((category) => (
                        <option key={category.id} value={category.id}>
                          {category.name}
                        </option>
                      ))}
                    </select>
                    <button
                      className="button-secondary"
                      type="button"
                      onClick={() => {
                        const categoryId = selectedCategoryByMovie[movie.id];
                        if (!categoryId) return;
                        addCategoryMutation.mutate({ movieId: movie.id, categoryId });
                      }}
                    >
                      Associar categoria
                    </button>
                  </>

                  <>
                    <button
                      type="button"
                      className="button-secondary"
                      onClick={() => {
                        setEditingMovieId(movie.id);
                        movieForm.reset({
                          title: movie.title,
                          description: movie.description ?? "",
                          genre: movie.genre ?? "",
                          director: movie.director ?? "",
                          movieType: movie.movieType ?? "movie",
                          releaseDate: movie.releaseDate ?? "",
                          durationMinutes: movie.durationMinutes ?? 120,
                          price: movie.price,
                          imageUrl: movie.imageUrl ?? "",
                          available: movie.available
                        });
                      }}
                    >
                      Editar
                    </button>
                    <button
                      type="button"
                      className="button-danger"
                      onClick={() => {
                        if (!window.confirm("Confirma exclusao deste filme?")) return;
                        deleteMovieMutation.mutate(movie.id);
                      }}
                    >
                      Excluir
                    </button>
                  </>
                </MovieCard>
              ))}
            </section>
          </>
        ) : null}

        {!isLoading && activeTab === "external" ? (
          <>
            <form
              className="form-grid card"
              onSubmit={(event) => {
                event.preventDefault();
                setError(null);
                setExternalSearchTerm(externalSearchInput.trim());
              }}
            >
              <h2>Importar do catálogo externo</h2>
              <input
                placeholder="Buscar titulo externo"
                value={externalSearchInput}
                onChange={(event) => setExternalSearchInput(event.target.value)}
              />
              <div className="actions">
                <button type="submit">Buscar</button>
                <button
                  type="button"
                  className="button-ghost"
                  onClick={() => {
                    setExternalSearchInput("");
                    setExternalSearchTerm("");
                    setError(null);
                  }}
                >
                  Limpar
                </button>
              </div>
            </form>

            {externalMoviesQuery.isLoading ? <p>Buscando filmes externos...</p> : null}
            {!externalMoviesQuery.isLoading && externalSearchTerm && externalMovies.length === 0 ? (
              <p>Nenhum filme externo encontrado.</p>
            ) : null}

            <section className="list">
              {externalMovies.map((movie) => (
                <article key={movie.externalId} className="card">
                  {movie.imageUrl ? <img src={movie.imageUrl} alt={movie.title} style={{ width: "100%", borderRadius: 12 }} /> : null}
                  <h2>{movie.title}</h2>
                  <p className="muted">
                    {movie.releaseYear || movie.releaseDate?.slice(0, 4) || "Ano indisponivel"} | {movie.movieType || "movie"}
                  </p>
                  <p>{movie.description || "Sem sinopse disponível nesta busca."}</p>
                  <p className="muted">Diretor: {movie.director || "-"}</p>
                  <p className="muted">Genero: {movie.genre || "-"}</p>
                  <div className="actions">
                    <button
                      type="button"
                      disabled={importExternalMutation.isPending}
                      onClick={() => importExternalMutation.mutate(movie.externalId)}
                    >
                      {importExternalMutation.isPending ? "Importando..." : "Importar para o catálogo"}
                    </button>
                  </div>
                </article>
              ))}
            </section>
          </>
        ) : null}

        {!isLoading && activeTab === "categories" ? (
          <>
            <form
              className="form-grid card"
              onSubmit={categoryForm.handleSubmit((values) => {
                setError(null);
                saveCategoryMutation.mutate({
                  id: editingCategoryId,
                  payload: normalizeCategoryPayload(values)
                });
              })}
            >
              <h2>{editingCategoryId ? "Editar categoria" : "Nova categoria"}</h2>
              <input placeholder="Nome" {...categoryForm.register("name")} />
              {categoryForm.formState.errors.name ? <p className="error">{categoryForm.formState.errors.name.message}</p> : null}
              <input placeholder="Descricao" {...categoryForm.register("description")} />
              {categoryForm.formState.errors.description ? <p className="error">{categoryForm.formState.errors.description.message}</p> : null}

              <div className="actions">
                <button type="submit" disabled={saveCategoryMutation.isPending}>
                  {saveCategoryMutation.isPending ? "Salvando..." : editingCategoryId ? "Atualizar" : "Criar"}
                </button>
                {editingCategoryId ? (
                  <button
                    className="button-ghost"
                    type="button"
                    onClick={() => {
                      categoryForm.reset(categoryDefaultValues);
                      setEditingCategoryId(null);
                    }}
                  >
                    Cancelar edicao
                  </button>
                ) : null}
              </div>
            </form>

            <section className="list">
              {categories.map((category) => (
                <article key={category.id} className="card">
                  <h2>{category.name}</h2>
                  <p>{category.description || "Sem descricao"}</p>
                  <div className="actions">
                    <button
                      type="button"
                      className="button-secondary"
                      onClick={() => {
                        setEditingCategoryId(category.id);
                        categoryForm.reset({
                          name: category.name,
                          description: category.description ?? ""
                        });
                      }}
                    >
                      Editar
                    </button>
                    <button
                      type="button"
                      className="button-danger"
                      onClick={() => {
                        if (!window.confirm("Confirma exclusao desta categoria?")) return;
                        deleteCategoryMutation.mutate(category.id);
                      }}
                    >
                      Excluir
                    </button>
                  </div>
                </article>
              ))}
            </section>
          </>
        ) : null}
      </section>
    </PageLayout>
  );
}
