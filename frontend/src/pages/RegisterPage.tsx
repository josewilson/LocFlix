import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../features/auth/useAuth";
import { mapApiError } from "../shared/api/errors";
import { useToast } from "../shared/components/ToastProvider";

export function RegisterPage() {
  const { signUp } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: ""
  });
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await signUp(form);
      showToast("Cadastro realizado. Faça login para continuar.", "success");
      navigate("/login", { replace: true });
    } catch (requestError) {
      const message = mapApiError(requestError, "Falha no cadastro. Confira os dados e tente novamente.");
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
          <h1>Crie sua conta e comece a locar.</h1>
          <p>
            Entre para uma plataforma de filmes com fluxo simples, visual premium e navegação pensada para desktop e mobile.
          </p>
          <div className="auth-points" aria-hidden="true">
            <span className="auth-point">Cadastro rápido com validação</span>
            <span className="auth-point">Acesso aos catálogos e locações</span>
            <span className="auth-point">Tema escuro com destaque vermelho</span>
          </div>
        </aside>

        <section className="auth-panel card">
          <p className="eyebrow">Novo usuário</p>
          <h2>Criar conta</h2>
          <form onSubmit={handleSubmit} className="form-grid">
            <input
              placeholder="Nome"
              value={form.firstName}
              onChange={(event) => setForm((old) => ({ ...old, firstName: event.target.value }))}
              required
            />
            <input
              placeholder="Sobrenome"
              value={form.lastName}
              onChange={(event) => setForm((old) => ({ ...old, lastName: event.target.value }))}
              required
            />
            <input
              type="email"
              placeholder="Email"
              value={form.email}
              onChange={(event) => setForm((old) => ({ ...old, email: event.target.value }))}
              required
            />
            <input
              type="password"
              placeholder="Senha"
              value={form.password}
              onChange={(event) => setForm((old) => ({ ...old, password: event.target.value }))}
              required
            />
            {error ? <p className="error">{error}</p> : null}
            <button type="submit" disabled={loading}>
              {loading ? "Criando..." : "Criar conta"}
            </button>
          </form>
          <p>
            Ja possui conta? <Link to="/login">Entrar</Link>
          </p>
        </section>
      </section>
    </main>
  );
}
