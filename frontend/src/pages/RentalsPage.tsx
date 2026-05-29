import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { cancelRental, getActiveRentals, getRentalHistory, returnRental } from "../shared/api/rentalsApi";
import { mapApiError } from "../shared/api/errors";
import { queryKeys } from "../shared/queries/queryKeys";
import { useToast } from "../shared/components/ToastProvider";
import { PageLayout } from "../shared/components/PageLayout";
import { formatCurrencyBRL, formatRentalStatus } from "../shared/formatters";

export function RentalsPage() {
  const queryClient = useQueryClient();
  const { showToast } = useToast();
  const [activeTab, setActiveTab] = useState<"active" | "history">("active");
  const [historyPage, setHistoryPage] = useState(0);
  const historyPageSize = 20;
  const [error, setError] = useState<string | null>(null);

  const activeRentalsQuery = useQuery({
    queryKey: queryKeys.rentals.active,
    queryFn: getActiveRentals
  });

  const historyRentalsQuery = useQuery({
    queryKey: queryKeys.rentals.history(historyPage, historyPageSize),
    queryFn: () => getRentalHistory(historyPage, historyPageSize)
  });

  const refreshRentals = async (): Promise<void> => {
    await queryClient.invalidateQueries({ queryKey: queryKeys.rentals.active });
    await queryClient.invalidateQueries({ queryKey: queryKeys.rentals.historyAll });
  };

  const returnMutation = useMutation({
    mutationFn: (rentalId: number) => returnRental(rentalId),
    onSuccess: async () => {
      setError(null);
      await refreshRentals();
      showToast("Locacao devolvida com sucesso.", "success");
    },
    onError: (requestError) => {
      const message = mapApiError(requestError, "Nao foi possivel concluir a devolucao.");
      setError(message);
      showToast(message, "error");
    }
  });

  const cancelMutation = useMutation({
    mutationFn: (rentalId: number) => cancelRental(rentalId),
    onSuccess: async () => {
      setError(null);
      await refreshRentals();
      showToast("Locacao cancelada com sucesso.", "success");
    },
    onError: (requestError) => {
      const message = mapApiError(requestError, "Nao foi possivel cancelar a locacao.");
      setError(message);
      showToast(message, "error");
    }
  });

  const activeRentals = activeRentalsQuery.data ?? [];
  const historyRentals = historyRentalsQuery.data?.content ?? [];
  const historyTotalPages = historyRentalsQuery.data?.totalPages ?? 0;
  const historyTotalElements = historyRentalsQuery.data?.totalElements ?? 0;
  const visibleRentals = activeTab === "active" ? activeRentals : historyRentals;
  const isLoading = activeTab === "active" ? activeRentalsQuery.isLoading : historyRentalsQuery.isLoading;
  const activeError = activeRentalsQuery.error ? mapApiError(activeRentalsQuery.error, "Nao foi possivel carregar as locacoes ativas.") : null;
  const historyError = historyRentalsQuery.error ? mapApiError(historyRentalsQuery.error, "Nao foi possivel carregar o historico de locacoes.") : null;

  return (
    <PageLayout
      title="Minhas locacoes"
      description="Acompanhe suas locacoes ativas, o historico e as acoes disponiveis em um painel unico."
    >
      <section className="card content-stack">

        <div className="actions">
          <button
            type="button"
            className={activeTab === "active" ? "button-secondary" : "button-ghost"}
            onClick={() => setActiveTab("active")}
          >
            Ativas ({activeRentals.length})
          </button>
          <button
            type="button"
            className={activeTab === "history" ? "button-secondary" : "button-ghost"}
            onClick={() => setActiveTab("history")}
          >
            Historico ({historyTotalElements})
          </button>
        </div>

        {isLoading ? <p>Carregando locacoes...</p> : null}
        {error ? <p className="error">{error}</p> : null}
        {activeTab === "active" && activeError ? <p className="error">{activeError}</p> : null}
        {activeTab === "history" && historyError ? <p className="error">{historyError}</p> : null}
        {!isLoading && visibleRentals.length === 0 ? <p>Nenhuma locacao encontrada nesta aba.</p> : null}

        <section className="list">
          {visibleRentals.map((rental) => (
            <article key={rental.id} className="card">
              <h2>{rental.movieTitle}</h2>
              <p className="muted">
                Inicio: {new Date(rental.rentalDate).toLocaleDateString("pt-BR")} | Vencimento: {new Date(rental.dueDate).toLocaleDateString("pt-BR")}
              </p>
              <p>
                <strong>Status:</strong> {formatRentalStatus(rental.status)} | <strong>Total:</strong> {formatCurrencyBRL(rental.totalPrice)}
              </p>

              {activeTab === "active" ? (
                <div className="actions">
                  <button type="button" onClick={() => returnMutation.mutate(rental.id)} disabled={returnMutation.isPending || cancelMutation.isPending}>
                    Devolver
                  </button>
                  <button
                    type="button"
                    className="button-danger"
                    onClick={() => cancelMutation.mutate(rental.id)}
                    disabled={returnMutation.isPending || cancelMutation.isPending}
                  >
                    Cancelar
                  </button>
                </div>
              ) : null}
            </article>
          ))}
        </section>

        {activeTab === "history" && historyTotalPages > 1 ? (
          <div className="pagination">
            <button type="button" disabled={historyPage === 0} onClick={() => setHistoryPage((current) => Math.max(current - 1, 0))}>
              Anterior
            </button>
            <span>
              Pagina {historyPage + 1} de {historyTotalPages}
            </span>
            <button
              type="button"
              disabled={historyPage + 1 >= historyTotalPages}
              onClick={() => setHistoryPage((current) => current + 1)}
            >
              Proxima
            </button>
          </div>
        ) : null}
      </section>
    </PageLayout>
  );
}
