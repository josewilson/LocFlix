import axios, { AxiosError } from "axios";
import { clearSession, getToken } from "../auth/session";
import type { ErrorResponse } from "../types/api";

const baseURL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export const httpClient = axios.create({
  baseURL,
  withCredentials: false,
  headers: {
    "Content-Type": "application/json"
  }
});

httpClient.interceptors.request.use((config) => {
  const token = getToken();

  config.headers = config.headers ?? {};

  if (token?.trim()) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

httpClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ErrorResponse>) => {
    const authorizationHeader = (error.config?.headers as { Authorization?: string; authorization?: string } | undefined)?.Authorization
      ?? (error.config?.headers as { Authorization?: string; authorization?: string } | undefined)?.authorization
      ?? "";

    if (error.response?.status === 401 && authorizationHeader.startsWith("Bearer ")) {
      clearSession();
      window.dispatchEvent(new CustomEvent("locflix:session-expired"));
    }

    return Promise.reject(error);
  }
);
