import { Navigate, useLocation } from "react-router-dom";
import type { ReactNode } from "react";
import { useAuth } from "../../features/auth/useAuth";

interface ProtectedRouteProps {
  children: ReactNode;
  requiredRoles?: string[];
}

export function ProtectedRoute({ children, requiredRoles }: ProtectedRouteProps) {
  const { isAuthenticated, hasRole } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }

  if (requiredRoles && requiredRoles.length > 0) {
    const isAuthorized = requiredRoles.some((role) => hasRole(role));
    if (!isAuthorized) {
      return <Navigate to="/movies" replace />;
    }
  }

  return <>{children}</>;
}
