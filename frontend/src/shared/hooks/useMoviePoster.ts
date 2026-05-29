import { useEffect, useState } from "react";
import { getMovieImageUrl } from "../api/movieImageService";

function escapeXml(value: string): string {
  return value
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&apos;");
}

function splitTitle(title: string): string[] {
  const words = title.trim().split(/\s+/).filter(Boolean);
  if (words.length === 0) {
    return ["Filme"];
  }

  const lines: string[] = [];
  let current = "";

  words.forEach((word) => {
    const candidate = current ? `${current} ${word}` : word;
    if (candidate.length <= 16) {
      current = candidate;
      return;
    }

    if (current) {
      lines.push(current);
    }
    current = word;
  });

  if (current) {
    lines.push(current);
  }

  return lines.slice(0, 3).map((line) => (line.length > 22 ? `${line.slice(0, 19)}...` : line));
}

/**
 * Gera uma capa SVG (data URL) a partir do título do filme.
 * Usada como fallback quando não há imagem real disponível.
 */
export function createPosterDataUrl(title: string): string {
  const lines = splitTitle(title);
  const titleSvg = lines
    .map((line, index) => `<tspan x="300" dy="${index === 0 ? 0 : 40}">${escapeXml(line)}</tspan>`)
    .join("");

  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 600 900">
      <defs>
        <linearGradient id="g" x1="0" x2="1" y1="0" y2="1">
          <stop offset="0%" stop-color="#111827" />
          <stop offset="100%" stop-color="#e50914" />
        </linearGradient>
      </defs>
      <rect width="600" height="900" rx="36" fill="url(#g)" />
      <rect x="48" y="48" width="504" height="804" rx="28" fill="rgba(255,255,255,0.06)" />
      <text x="300" y="130" text-anchor="middle" fill="#fecaca" font-family="Arial, Helvetica, sans-serif" font-size="20" letter-spacing="6">LOCFLIX</text>
      <text x="300" y="450" text-anchor="middle" fill="white" font-family="Arial, Helvetica, sans-serif" font-size="38" font-weight="700">${titleSvg}</text>
      <text x="300" y="645" text-anchor="middle" fill="#f8fafc" font-family="Arial, Helvetica, sans-serif" font-size="26">Capa gerada pelo título</text>
    </svg>
  `;

  return "data:image/svg+xml;charset=UTF-8," + encodeURIComponent(svg);
}

interface UseMoviePosterResult {
  /** URL final da imagem (real ou SVG gerado). */
  posterSrc: string;
  /** true enquanto a imagem real está sendo resolvida. */
  isLoadingImage: boolean;
  /** Handler para o onError do <img> — troca para o SVG gerado. */
  handleImageError: (event: { currentTarget: HTMLImageElement }) => void;
  /** Handler para o onLoad do <img>. */
  handleImageLoad: (event: { currentTarget: HTMLImageElement }) => void;
}

/**
 * Resolve o pôster de um filme: usa `imageUrl` quando existe, senão tenta a
 * API externa (OMDB) e cai para um SVG gerado a partir do título.
 */
export function useMoviePoster(title: string, imageUrl?: string): UseMoviePosterResult {
  const [posterSrc, setPosterSrc] = useState<string>(imageUrl || createPosterDataUrl(title));
  const [isLoadingImage, setIsLoadingImage] = useState(!imageUrl);

  useEffect(() => {
    if (imageUrl) {
      setPosterSrc(imageUrl);
      setIsLoadingImage(false);
      return;
    }

    let isMounted = true;
    setIsLoadingImage(true);

    const fetchImage = async () => {
      try {
        const resolved = await getMovieImageUrl(title, imageUrl);
        if (isMounted) {
          setPosterSrc(resolved ?? createPosterDataUrl(title));
        }
      } catch {
        if (isMounted) {
          setPosterSrc(createPosterDataUrl(title));
        }
      } finally {
        if (isMounted) {
          setIsLoadingImage(false);
        }
      }
    };

    fetchImage();

    return () => {
      isMounted = false;
    };
  }, [title, imageUrl]);

  function handleImageError(event: { currentTarget: HTMLImageElement }): void {
    event.currentTarget.src = createPosterDataUrl(title);
    event.currentTarget.classList.remove("loading");
  }

  function handleImageLoad(event: { currentTarget: HTMLImageElement }): void {
    event.currentTarget.classList.remove("loading");
  }

  return { posterSrc, isLoadingImage, handleImageError, handleImageLoad };
}
