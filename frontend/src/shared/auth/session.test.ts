import { beforeEach, describe, expect, it, vi } from "vitest";
import { clearSession, loadSession, saveSession } from "./session";
import type { LoginResponse } from "../types/api";

function createSession(expiresIn = 3_600_000): LoginResponse {
  return {
    token: "token-teste",
    type: "Bearer",
    expiresIn,
    user: {
      id: 1,
      email: "user@example.com",
      firstName: "Test",
      fullName: "Test User",
      active: true,
      roles: ["ADMIN"],
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
  };
}

describe("session", () => {
  beforeEach(() => {
    sessionStorage.clear();
    vi.useRealTimers();
  });

  it("salva e recupera sessao valida", () => {
    const session = createSession();
    saveSession(session);

    expect(loadSession()?.token).toBe("token-teste");
  });

  it("descarta sessao expirada", () => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date("2026-04-21T12:00:00Z"));
    saveSession(createSession(1000));

    vi.setSystemTime(new Date("2026-04-21T12:00:02Z"));
    expect(loadSession()).toBeNull();
  });

  it("limpa sessao explicitamente", () => {
    saveSession(createSession());
    clearSession();

    expect(loadSession()).toBeNull();
  });
});

