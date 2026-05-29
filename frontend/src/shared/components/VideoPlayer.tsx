import { useEffect, useRef, useState } from "react";
import Hls from "hls.js";

interface VideoPlayerProps {
  src: string;
  title: string;
  onBack: () => void;
}

export function VideoPlayer({ src, title, onBack }: VideoPlayerProps) {
  const videoRef = useRef<HTMLVideoElement>(null);
  const hlsRef = useRef<Hls | null>(null);
  const [isReady, setIsReady] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const video = videoRef.current;
    if (!video) return;

    setError(null);
    setIsReady(false);

    if (Hls.isSupported()) {
      const hls = new Hls({ enableWorker: true, lowLatencyMode: false });
      hlsRef.current = hls;

      hls.loadSource(src);
      hls.attachMedia(video);

      hls.on(Hls.Events.MANIFEST_PARSED, () => setIsReady(true));
      hls.on(Hls.Events.ERROR, (_event, data) => {
        if (data.fatal) {
          setError("Erro ao carregar o vídeo. Verifique se o processamento foi concluído.");
        }
      });
    } else if (video.canPlayType("application/vnd.apple.mpegurl")) {
      // Safari com suporte nativo a HLS
      video.src = src;
      video.addEventListener("loadedmetadata", () => setIsReady(true));
    } else {
      setError("Seu navegador não suporta streaming HLS.");
    }

    return () => {
      hlsRef.current?.destroy();
      hlsRef.current = null;
    };
  }, [src]);

  function toggleFullscreen() {
    if (!containerRef.current) return;
    if (!document.fullscreenElement) {
      containerRef.current.requestFullscreen();
      setIsFullscreen(true);
    } else {
      document.exitFullscreen();
      setIsFullscreen(false);
    }
  }

  return (
    <div ref={containerRef} className="video-player-container">
      <div className="video-player-header">
        <button className="video-back-btn" onClick={onBack} type="button">
          ← Voltar
        </button>
        <span className="video-title">{title}</span>
        <button className="video-fullscreen-btn" onClick={toggleFullscreen} type="button">
          {isFullscreen ? "⊡" : "⛶"}
        </button>
      </div>

      {error ? (
        <div className="video-error">
          <p>{error}</p>
        </div>
      ) : (
        <>
          {!isReady && (
            <div className="video-loading">
              <div className="video-spinner" />
              <p>Carregando vídeo...</p>
            </div>
          )}
          <video
            ref={videoRef}
            controls
            autoPlay
            className={`video-element ${isReady ? "visible" : "hidden"}`}
            poster=""
          />
        </>
      )}
    </div>
  );
}
