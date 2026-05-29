interface FavoriteMovieActionProps {
  isFavorited: boolean;
  isPending: boolean;
  onToggle: () => void;
}

export function FavoriteMovieAction({ isFavorited, isPending, onToggle }: FavoriteMovieActionProps) {
  return (
    <button
      type="button"
      className={isFavorited ? "button-secondary" : "button-ghost"}
      onClick={onToggle}
      disabled={isPending}
      aria-pressed={isFavorited}
      aria-busy={isPending}
    >
      {isFavorited ? "Remover dos favoritos" : "Favoritar"}
    </button>
  );
}

