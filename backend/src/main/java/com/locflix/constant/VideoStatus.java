package com.locflix.constant;

public enum VideoStatus {
    PENDING("Aguardando processamento"),
    PROCESSING("Processando"),
    READY("Pronto"),
    ERROR("Erro no processamento");

    private final String displayName;

    VideoStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
