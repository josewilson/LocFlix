import { FormEvent, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../features/auth/useAuth";
import { mapApiError } from "../shared/api/errors";
import { useToast } from "../shared/components/ToastProvider";

export function LoginPage() {
  const { signIn } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const returnTo = (location.state as { from?: string } | null)?.from ?? "/movies";

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await signIn(email, password);
      showToast("Login realizado com sucesso.", "success");
      navigate(returnTo, { replace: true });
    } catch (requestError) {
      const message = mapApiError(requestError, "Falha no login. Verifique email e senha.");
      setError(message);
      showToast(message, "error");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-shell">
        <aside className="auth-hero">
          <p className="eyebrow">LOCFLIX</p>
          <h1>Entre e volte ao seu catálogo.</h1>
          <p>
            Uma experiência única, direta e cinematográfica para explorar filmes, acompanhar locações e acessar o painel administrativo.
          </p>
          <div className="auth-points" aria-hidden="true">
            <span className="auth-point">Catálogo com busca e filtros rápidos</span>
            <span className="auth-point">Locações, devoluções e histórico em um clique</span>
            <span className="auth-point">Interface responsiva com identidade visual forte</span>
          </div>
        </aside>

        <section className="auth-panel card">
          <p className="eyebrow">Acesso seguro</p>
          <h2>Entrar no LOCFLIX</h2>
          <form onSubmit={handleSubmit} className="form-grid">
            <input type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} required />
            <input
              type="password"
              placeholder="Senha"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            {error ? <p className="error">{error}</p> : null}
            <button type="submit" disabled={loading}>
              {loading ? "Entrando..." : "Entrar"}
            </button>
          </form>
          <p>
            Sem conta? <Link to="/register">Criar conta</Link>
          </p>
        </section>
      </section>
    </main>
  );
}
