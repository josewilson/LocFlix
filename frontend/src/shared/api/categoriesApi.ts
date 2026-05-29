import { httpClient } from "./httpClient";
import type { CategoryResponse, CreateCategoryRequest, PageResponse } from "../types/api";

export async function fetchCategories(page = 0, size = 20): Promise<PageResponse<CategoryResponse>> {
  const { data } = await httpClient.get<PageResponse<CategoryResponse>>("/api/v1/categories", {
    params: { page, size }
  });
  return data;
}

export async function createCategory(payload: CreateCategoryRequest): Promise<CategoryResponse> {
  const { data } = await httpClient.post<CategoryResponse>("/api/v1/categories", payload);
  return data;
}

export async function updateCategory(id: number, payload: CreateCategoryRequest): Promise<CategoryResponse> {
  const { data } = await httpClient.put<CategoryResponse>(`/api/v1/categories/${id}`, payload);
  return data;
}

export async function deleteCategory(id: number): Promise<void> {
  await httpClient.delete(`/api/v1/categories/${id}`);
}

