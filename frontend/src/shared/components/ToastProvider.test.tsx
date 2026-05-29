import { fireEvent, render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { ToastProvider, useToast } from "./ToastProvider";

function TestComponent() {
  const { showToast } = useToast();

  return (
    <button onClick={() => showToast("Operacao concluida", "success")}>Disparar toast</button>
  );
}

describe("ToastProvider", () => {
  it("renderiza toast quando showToast e chamado", () => {
    render(
      <ToastProvider>
        <TestComponent />
      </ToastProvider>
    );

    fireEvent.click(screen.getByText("Disparar toast"));

    expect(screen.getByText("Operacao concluida")).toBeInTheDocument();
  });
});

