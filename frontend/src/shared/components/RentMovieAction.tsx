interface RentMovieActionProps {
  movieId: number;
  isAvailable: boolean;
  isPending: boolean;
  isCurrentMoviePending?: boolean;
  onRent: (movieId: number) => void;
  showProcessingText?: boolean;
}

export function RentMovieAction({
  movieId,
  isAvailable,
  isPending,
  isCurrentMoviePending = false,
  onRent,
  showProcessingText = false
}: RentMovieActionProps) {
  return (
    <>
      {showProcessingText && isCurrentMoviePending ? <span className="muted">Processando locacao...</span> : null}
      <button type="button" onClick={() => onRent(movieId)} disabled={!isAvailable || isPending} aria-busy={isCurrentMoviePending}>
        {isCurrentMoviePending ? "Alugando..." : "Alugar"}
      </button>
    </>
  );
}

