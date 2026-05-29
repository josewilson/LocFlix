import { httpClient } from "./httpClient";
import type { CreateRentalRequest, PageResponse, RentalResponse } from "../types/api";

export async function getUserRentals(page = 0, size = 10): Promise<PageResponse<RentalResponse>> {
  const { data } = await httpClient.get<PageResponse<RentalResponse>>("/api/v1/rentals", {
    params: { page, size }
  });
  return data;
}

export async function getActiveRentals(): Promise<RentalResponse[]> {
  const { data } = await httpClient.get<RentalResponse[]>("/api/v1/rentals/active");
  return data;
}

export async function getRentalHistory(page = 0, size = 10): Promise<PageResponse<RentalResponse>> {
  const { data } = await httpClient.get<PageResponse<RentalResponse>>("/api/v1/rentals/history", {
    params: { page, size }
  });
  return data;
}

export async function getOverdueRentals(page = 0, size = 10): Promise<PageResponse<RentalResponse>> {
  const { data } = await httpClient.get<PageResponse<RentalResponse>>("/api/v1/rentals/overdue", {
    params: { page, size }
  });
  return data;
}

export async function createRental(payload: CreateRentalRequest): Promise<RentalResponse> {
  const { data } = await httpClient.post<RentalResponse>("/api/v1/rentals", payload);
  return data;
}

export async function returnRental(rentalId: number): Promise<RentalResponse> {
  const { data } = await httpClient.put<RentalResponse>(`/api/v1/rentals/${rentalId}/return`);
  return data;
}

export async function cancelRental(rentalId: number): Promise<RentalResponse> {
  const { data } = await httpClient.delete<RentalResponse>(`/api/v1/rentals/${rentalId}`);
  return data;
}

