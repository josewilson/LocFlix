import { httpClient } from "./httpClient";
import type { VideoResponse } from "../types/api";

const BASE = "/api/v1/videos";

export async function getVideoStatus(movieId: number): Promise<VideoResponse> {
  const { data } = await httpClient.get<VideoResponse>(`${BASE}/${movieId}/status`);
  return data;
}

export async function uploadVideo(movieId: number, file: File): Promise<VideoResponse> {
  const form = new FormData();
  form.append("file", file);
  const { data } = await httpClient.post<VideoResponse>(`${BASE}/upload/${movieId}`, form, {
    headers: { "Content-Type": "multipart/form-data" }
  });
  return data;
}

export function buildStreamUrl(videoId: number): string {
  const base = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";
  return `${base}/api/v1/videos/stream/${videoId}/master.m3u8`;
}
