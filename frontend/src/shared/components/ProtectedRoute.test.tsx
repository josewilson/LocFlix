import { render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { describe, expect, it, vi } from "vitest";
import { ProtectedRoute } from "./ProtectedRoute";

const mockedUseAuth = vi.hoisted(() => vi.fn());

vi.mock("../../features/auth/useAuth", () => ({
  useAuth: mockedUseAuth
}));

describe("ProtectedRoute", () => {
  it("redireciona para login quando nao autenticado", () => {
    mockedUseAuth.mockReturnValue({
      isAuthenticated: false,
      hasRole: () => false
    });

    render(
      <MemoryRouter initialEntries={["/admin"]}>
        <Routes>
          <Route
            path="/admin"
            element={
              <ProtectedRoute requiredRoles={["ADMIN"]}>
                <div>Admin page</div>
              </ProtectedRoute>
            }
          />
          <Route path="/login" element={<div>Login page</div>} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText("Login page")).toBeInTheDocument();
  });

  it("renderiza children quando autenticado e autorizado", () => {
    mockedUseAuth.mockReturnValue({
      isAuthenticated: true,
      hasRole: () => true
    });

    render(
      <MemoryRouter initialEntries={["/admin"]}>
        <Routes>
          <Route
            path="/admin"
            element={
              <ProtectedRoute requiredRoles={["ADMIN"]}>
                <div>Admin page</div>
              </ProtectedRoute>
            }
          />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText("Admin page")).toBeInTheDocument();
  });
});

