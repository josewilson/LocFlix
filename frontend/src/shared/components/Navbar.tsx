import { useEffect, useState, type FormEvent } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../features/auth/useAuth";

const navItems = [
  { to: "/", label: "Início", end: true },
  { to: "/movies", label: "Filmes" },
  { to: "/favorites", label: "Favoritos" },
  { to: "/rentals", label: "Minhas locações" }
];

function getInitials(name?: string): string {
  if (!name) return "?";
  const parts = name.trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return "?";
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

export function Navbar() {
  const navigate = useNavigate();
  const { user, hasRole, signOut } = useAuth();
  const [scrolled, setScrolled] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 20);
    handleScroll();
    window.addEventListener("scroll", handleScroll, { passive: true });
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  function handleSearchSubmit(event: FormEvent<HTMLFormElement>): void {
    event.preventDefault();
    const term = searchTerm.trim();
    navigate(term ? `/movies?title=${encodeURIComponent(term)}` : "/movies");
  }

  function handleSignOut(): void {
    signOut();
    navigate("/login", { replace: true });
  }

  return (
    <header className={scrolled ? "navbar navbar-scrolled" : "navbar"}>
      <div className="navbar-inner">
        <NavLink to="/" className="navbar-brand" aria-label="LOCFLIX início">
          LOCFLIX
        </NavLink>

        <nav className="navbar-links" aria-label="Navegação principal">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              className={({ isActive }) => (isActive ? "navbar-link active" : "navbar-link")}
            >
              {item.label}
            </NavLink>
          ))}
          {hasRole("ADMIN") ? (
            <NavLink to="/admin" className={({ isActive }) => (isActive ? "navbar-link active" : "navbar-link")}>
              Admin
            </NavLink>
          ) : null}
        </nav>

        <div className="navbar-actions">
          <form className="navbar-search" onSubmit={handleSearchSubmit} role="search">
            <input
              type="search"
              placeholder="Buscar títulos"
              value={searchTerm}
              onChange={(event) => setSearchTerm(event.target.value)}
              aria-label="Buscar títulos"
            />
            <button type="submit" aria-label="Buscar">
              🔍
            </button>
          </form>

          <div className="navbar-user" title={user?.fullName ?? user?.email}>
            <span className="navbar-avatar" aria-hidden="true">
              {getInitials(user?.fullName ?? user?.email)}
            </span>
            <button type="button" className="button-ghost navbar-signout" onClick={handleSignOut}>
              Sair
            </button>
          </div>
        </div>
      </div>
    </header>
  );
}
