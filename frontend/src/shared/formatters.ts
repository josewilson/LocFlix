const brlFormatter = new Intl.NumberFormat("pt-BR", {
  style: "currency",
  currency: "BRL"
});

const rentalStatusLabels: Record<string, string> = {
  ACTIVE: "Ativa",
  COMPLETED: "Concluída",
  OVERDUE: "Em atraso",
  CANCELLED: "Cancelada"
};

export function formatCurrencyBRL(value: number): string {
  return brlFormatter.format(value);
}

export function formatRentalStatus(status: string): string {
  return rentalStatusLabels[status] ?? status;
}

