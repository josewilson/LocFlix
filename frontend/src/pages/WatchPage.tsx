import { useNavigate, useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { getActiveRentals } from "../shared/api/rentalsApi";
import { getVideoStatus, buildStreamUrl } from "../shared/api/videoApi";
import { queryKeys } from "../shared/queries/queryKeys";
import { VideoPlayer } from "../shared/components/VideoPlayer";

export function WatchPage() {
  const { movieId } = useParams<{ movieId: string }>();
  const navigate = useNavigate();
  const id = Number(movieId);

  const activeRentalsQuery = useQuery({
    queryKey: queryKeys.rentals.active,
    queryFn: getActiveRentals
  });

  const videoQuery = useQuery({
    queryKey: queryKeys.videos.status(id),
    queryFn: () => getVideoStatus(id),
    enabled: !!id,
    refetchInterval: (query) => {
      const status = query.state.data?.status;
      return status === "PENDING" || status === "PROCESSING" ? 3000 : false;
    }
  });

  const hasActiveRental = activeRentalsQuery.data?.some((r) => r.movieId === id) ?? false;
  const isLoading = activeRentalsQuery.isLoading || videoQuery.isLoading;
  const video = videoQuery.data;

  if (isLoading) {
    return (
      <div className="watch-loading">
        <div className="video-spinner" />
        <p>Verificando acesso...</p>
      </div>
    );
  }

  if (!hasActiveRental) {
    return (
      <div className="watch-blocked">
        <h2>Acesso negado</h2>
        <p>Você precisa ter uma locação ativa deste filme para assistir.</p>
        <button type="button" onClick={() => navigate("/movies")}>
          Ver filmes disponíveis
        </button>
      </div>
    );
  }

  if (!video) {
    return (
      <div className="watch-blocked">
        <h2>Vídeo não disponível</h2>
        <p>Este filme ainda não possui vídeo cadastrado.</p>
        <button type="button" onClick={() => navigate("/movies")}>
          Voltar aos filmes
        </button>
      </div>
    );
  }

  if (video.status === "PENDING" || video.status === "PROCESSING") {
    return (
      <div className="watch-loading">
        <div className="video-spinner" />
        <h2>Processando vídeo...</h2>
        <p>Status: <strong>{video.statusDisplay}</strong>. Aguarde, a página será atualizada automaticamente.</p>
      </div>
    );
  }

  if (video.status === "ERROR") {
    return (
      <div className="watch-blocked">
        <h2>Erro no vídeo</h2>
        <p>{video.errorMessage ?? "Ocorreu um erro no processamento. Contate o suporte."}</p>
        <button type="button" onClick={() => navigate("/movies")}>
          Voltar aos filmes
        </button>
      </div>
    );
  }

  return (
    <VideoPlayer
      src={buildStreamUrl(video.id)}
      title={video.movieTitle}
      onBack={() => navigate("/movies")}
    />
  );
}
