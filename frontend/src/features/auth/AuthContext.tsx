import { createContext, useEffect, useMemo, useState } from "react";
import type { ReactNode } from "react";
import type { LoginResponse, UserResponse } from "../../shared/types/api";
import { clearSession, loadSession, saveSession } from "../../shared/auth/session";
import { login as loginRequest, register as registerRequest } from "../../shared/api/authApi";

interface AuthContextValue {
  user: UserResponse | null;
  roles: string[];
  isAuthenticated: boolean;
  signIn: (email: string, password: string) => Promise<void>;
  signUp: (payload: { email: string; password: string; firstName: string; lastName: string }) => Promise<void>;
  signOut: () => void;
  hasRole: (role: string) => boolean;
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const initialSession = loadSession();
  const [session, setSession] = useState<LoginResponse | null>(initialSession);

  useEffect(() => {
    const handler = () => setSession(null);
    window.addEventListener("locflix:session-expired", handler);
    return () => window.removeEventListener("locflix:session-expired", handler);
  }, []);

  async function signIn(email: string, password: string): Promise<void> {
    const data = await loginRequest({ email, password });
    saveSession(data);
    setSession(data);
  }

  async function signUp(payload: { email: string; password: string; firstName: string; lastName: string }): Promise<void> {
    await registerRequest(payload);
  }

  function signOut(): void {
    clearSession();
    setSession(null);
  }

  function hasRole(role: string): boolean {
    const normalizedRole = role.replace("ROLE_", "").toUpperCase();
    return (session?.user.roles ?? []).some((currentRole) => {
      const normalizedCurrent = currentRole.replace("ROLE_", "").toUpperCase();
      return normalizedCurrent === normalizedRole;
    });
  }

  const value = useMemo<AuthContextValue>(
    () => ({
      user: session?.user ?? null,
      roles: session?.user.roles ?? [],
      isAuthenticated: Boolean(session?.token),
      signIn,
      signUp,
      signOut,
      hasRole
    }),
    [session]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
