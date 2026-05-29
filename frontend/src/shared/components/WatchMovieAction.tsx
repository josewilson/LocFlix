import { useNavigate } from "react-router-dom";

interface WatchMovieActionProps {
  movieId: number;
  hasActiveRental: boolean;
}

export function WatchMovieAction({ movieId, hasActiveRental }: WatchMovieActionProps) {
  const navigate = useNavigate();

  if (!hasActiveRental) return null;

  return (
    <button
      type="button"
      className="btn-watch"
      onClick={() => navigate(`/watch/${movieId}`)}
    >
      ▶ Assistir
    </button>
  );
}
