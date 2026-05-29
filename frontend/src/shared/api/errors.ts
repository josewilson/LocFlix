import axios from "axios";
import type { ErrorResponse } from "../types/api";

export function mapApiError(error: unknown, fallbackMessage = "Nao foi possivel concluir a operacao."): string {
  if (axios.isAxiosError<ErrorResponse>(error)) {
    const apiError = error.response?.data;

    if (apiError?.fieldErrors && Object.keys(apiError.fieldErrors).length > 0) {
      const firstFieldError = Object.values(apiError.fieldErrors)[0];
      if (firstFieldError) {
        return firstFieldError;
      }
    }

    if (apiError?.errors && apiError.errors.length > 0) {
      return apiError.errors[0];
    }

    if (apiError?.message) {
      return apiError.message;
    }
  }

  return fallbackMessage;
}

