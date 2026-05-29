import { describe, expect, it } from "vitest";
import { formatCurrencyBRL, formatRentalStatus } from "./formatters";

describe("formatters", () => {
  it("formats currency in BRL", () => {
    expect(formatCurrencyBRL(1234.56)).toMatch(/^R\$\s?1\.234,56$/);
  });

  it("formats known rental statuses", () => {
    expect(formatRentalStatus("ACTIVE")).toBe("Ativa");
    expect(formatRentalStatus("COMPLETED")).toBe("Concluída");
    expect(formatRentalStatus("OVERDUE")).toBe("Em atraso");
    expect(formatRentalStatus("CANCELLED")).toBe("Cancelada");
  });

  it("keeps unknown rental statuses unchanged", () => {
    expect(formatRentalStatus("PAUSED")).toBe("PAUSED");
  });
});

