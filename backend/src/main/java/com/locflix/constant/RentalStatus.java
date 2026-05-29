package com.locflix.constant;

/**
 * Enumeração para status de locação.
 */
public enum RentalStatus {
    ACTIVE("Ativa"),
    COMPLETED("Concluída"),
    OVERDUE("Atrasada"),
    CANCELLED("Cancelada");

    private final String displayName;

    RentalStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

