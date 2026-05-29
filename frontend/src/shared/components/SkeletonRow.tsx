interface SkeletonRowProps {
  /** Quantidade de pôsteres-fantasma exibidos. */
  count?: number;
}

/**
 * Placeholder com efeito shimmer exibido enquanto os carrosséis carregam.
 */
export function SkeletonRow({ count = 6 }: SkeletonRowProps) {
  return (
    <section className="carousel">
      <div className="skeleton skeleton-title" />
      <div className="carousel-track skeleton-track">
        {Array.from({ length: count }).map((_, index) => (
          <div key={index} className="skeleton skeleton-poster" />
        ))}
      </div>
    </section>
  );
}
