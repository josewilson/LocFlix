import { Navigate, Route, Routes } from "react-router-dom";
import { LoginPage } from "../pages/LoginPage";
import { RegisterPage } from "../pages/RegisterPage";
import { HomePage } from "../pages/HomePage";
import { MoviesPage } from "../pages/MoviesPage";
import { MovieDetailPage } from "../pages/MovieDetailPage";
import { FavoritesPage } from "../pages/FavoritesPage";
import { RentalsPage } from "../pages/RentalsPage";
import { AdminPage } from "../pages/AdminPage";
import { WatchPage } from "../pages/WatchPage";
import { ProtectedRoute } from "../shared/components/ProtectedRoute";

export function AppRouter() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <HomePage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/movie/:id"
        element={
          <ProtectedRoute>
            <MovieDetailPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/movies"
        element={
          <ProtectedRoute>
            <MoviesPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/rentals"
        element={
          <ProtectedRoute>
            <RentalsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/favorites"
        element={
          <ProtectedRoute>
            <FavoritesPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin"
        element={
          <ProtectedRoute requiredRoles={["ADMIN"]}>
            <AdminPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/watch/:movieId"
        element={
          <ProtectedRoute>
            <WatchPage />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
