package org.example.app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class BaseEntity {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    protected Long id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    private int status; // 1 = ativo | 2 = inativo

    protected BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID não pode ser alterado.");
        }
        this.id = id;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }


    public String getCreatedAtFormatted() {
        return createdAt != null ? createdAt.format(FORMATTER) : "";
    }

    public String getUpdatedAtFormatted() {
        return updatedAt != null ? updatedAt.format(FORMATTER) : "";
    }

    public void markAsUpdated() {
        this.updatedAt = LocalDateTime.now();
    }

    public int getStatus() { return status; }
    public void setStatus(int status) {
        if (status != 1 && status != 2) {
            throw new IllegalArgumentException("Status inválido. Use 1 (ativo) ou 2 (inativo).");
        }
        this.status = status;
    }
}
