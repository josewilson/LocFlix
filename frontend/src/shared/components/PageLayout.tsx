import type { ReactNode } from "react";
import { Navbar } from "./Navbar";

interface PageLayoutProps {
  title: string;
  description?: string;
  /** Banner full-bleed exibido logo abaixo da Navbar (ex.: HeroBanner). Opcional. */
  hero?: ReactNode;
  children: ReactNode;
}

export function PageLayout({ title, description, hero, children }: PageLayoutProps) {
  return (
    <>
      <Navbar />
      {hero}
      <main className={hero ? "app-shell app-shell--hero" : "app-shell"}>
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
