import { httpClient } from "./httpClient";
import type { LoginResponse, UserResponse } from "../types/api";

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export async function login(payload: LoginRequest): Promise<LoginResponse> {
  const { data } = await httpClient.post<LoginResponse>("/api/v1/auth/login", payload);
  return data;
}

export async function register(payload: RegisterRequest): Promise<UserResponse> {
  const { data } = await httpClient.post<UserResponse>("/api/v1/auth/register", payload);
  return data;
}

