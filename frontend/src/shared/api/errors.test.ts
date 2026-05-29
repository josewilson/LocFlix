import { AxiosError } from "axios";
import { describe, expect, it } from "vitest";
import { mapApiError } from "./errors";
import type { ErrorResponse } from "../types/api";

describe("mapApiError", () => {
  it("retorna mensagem de fieldErrors quando existir", () => {
    const apiError: ErrorResponse = {
      timestamp: new Date().toISOString(),
      status: 400,
      statusDescription: "Bad Request",
      message: "Dados invalidos",
      path: "/api/v1/auth/register",
      fieldErrors: { email: "Email invalido" }
    };

    const error = new AxiosError<ErrorResponse>("Request failed", "400", undefined, undefined, {
      data: apiError,
      status: 400,
      statusText: "Bad Request",
      headers: {},
      config: { headers: {} as never }
    });

    expect(mapApiError(error)).toBe("Email invalido");
  });

  it("retorna fallback para erro desconhecido", () => {
    expect(mapApiError(new Error("Falha"), "Mensagem padrao")).toBe("Mensagem padrao");
  });
});

