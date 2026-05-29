import type { ReactNode } from "react";
import { Navbar } from "./Navbar";

interface PageLayoutProps {
  title: string;
  description?: string;
  children: ReactNode;
}

export function PageLayout({ title, description, children }: PageLayoutProps) {
  return (
    <>
      <Navbar />
      <main className="app-shell">
        <section className="page-card">
          <header className="page-header">
            <div>
              <p className="eyebrow">LOCFLIX</p>
              <h1>{title}</h1>
              {description ? <p className="muted">{description}</p> : null}
            </div>
          </header>

          <div className="page-content">{children}</div>
        </section>
      </main>
    </>
  );
}
