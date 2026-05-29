import type { LoginResponse } from "../types/api";

const STORAGE_KEY = "locflix.session";

interface StoredSession {
  session: LoginResponse;
  savedAt: number;
}

export function saveSession(session: LoginResponse): void {
  const payload: StoredSession = {
    session,
    savedAt: Date.now(),
  };
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(payload));
}

export function loadSession(): LoginResponse | null {
  const raw = sessionStorage.getItem(STORAGE_KEY);
  if (!raw) return null;

  try {
    const parsed = JSON.parse(raw) as StoredSession | LoginResponse;
    const normalized = "session" in parsed ? parsed : { session: parsed, savedAt: Date.now() };

    const expiresAt = normalized.savedAt + normalized.session.expiresIn;
    if (Date.now() >= expiresAt) {
      sessionStorage.removeItem(STORAGE_KEY);
      return null;
    }

    return normalized.session;
  } catch {
    sessionStorage.removeItem(STORAGE_KEY);
    return null;
  }
}

export function clearSession(): void {
  sessionStorage.removeItem(STORAGE_KEY);
}

export function getToken(): string | null {
  return loadSession()?.token ?? null;
}
