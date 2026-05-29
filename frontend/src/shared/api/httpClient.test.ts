import { AxiosError } from "axios";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

vi.mock("../auth/session", () => ({
  clearSession: vi.fn(),
  getToken: vi.fn()
}));

import { clearSession, getToken } from "../auth/session";
import { httpClient } from "./httpClient";
import type { ErrorResponse } from "../types/api";

const mockedGetToken = vi.mocked(getToken);
const mockedClearSession = vi.mocked(clearSession);

// Os handlers internos dos interceptors não são tipados publicamente pelo axios.
type InterceptorManager = {
  handlers: Array<{ fulfilled: (value: unknown) => unknown; rejected: (error: unknown) => unknown }>;
};

describe("httpClient", () => {
  beforeEach(() => {
    mockedGetToken.mockReset();
    mockedClearSession.mockReset();
    vi.spyOn(window, "dispatchEvent").mockImplementation(() => true);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("adiciona Authorization quando existe token", async () => {
    mockedGetToken.mockReturnValue("token-123");
    const requestInterceptor = (httpClient.interceptors.request as unknown as InterceptorManager).handlers[0].fulfilled;

    const config = (await requestInterceptor({ headers: {} })) as { headers: { Authorization?: string } };

    expect(config.headers.Authorization).toBe("Bearer token-123");
  });


  it("limpa a sessão e dispara evento quando recebe 401 com token", async () => {
    const responseInterceptor = (httpClient.interceptors.response as unknown as InterceptorManager).handlers[0].rejected;

    const apiError: ErrorResponse = {
      timestamp: new Date().toISOString(),
      status: 401,
      statusDescription: "Unauthorized",
      message: "Token expirado",
      path: "/api/v1/movies"
    };

    const requestConfig = { headers: { Authorization: "Bearer token-123" } as never };
    const error = new AxiosError<ErrorResponse>("Unauthorized", "401", requestConfig, undefined, {
      data: apiError,
      status: 401,
      statusText: "Unauthorized",
      headers: {},
      config: requestConfig
    });

    await expect(responseInterceptor(error)).rejects.toBe(error);
    expect(mockedClearSession).toHaveBeenCalledTimes(1);
    expect(window.dispatchEvent).toHaveBeenCalledTimes(1);
  });
});

