export const queryKeys = {
  movies: {
    all: ["movies"] as const,
    list: (params: { page: number; size: number; title: string; onlyAvailable: boolean }) =>
      ["movies", "list", params] as const,
    adminList: (page: number, size: number) => ["movies", "admin", page, size] as const,
    externalSearch: (title: string) => ["movies", "external", title] as const,
    detail: (id: number) => ["movies", "detail", id] as const,
    popular: ["movies", "popular"] as const
  },
  rentals: {
    active: ["rentals", "active"] as const,
    historyAll: ["rentals", "history"] as const,
    history: (page: number, size: number) => ["rentals", "history", page, size] as const,
    overdue: (page: number, size: number) => ["rentals", "overdue", page, size] as const
  },
  categories: {
    list: (page: number, size: number) => ["categories", "list", page, size] as const
  },
  favorites: {
    list: ["favorites", "list"] as const
  },
  videos: {
    status: (movieId: number) => ["videos", "status", movieId] as const
  }
};

