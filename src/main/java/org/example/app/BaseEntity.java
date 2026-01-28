package org.example.app;
import java.time.LocalDateTime;

public abstract class BaseEntity {

    protected Long id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    protected BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID não pode ser alterado.");
        }
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void markAsUpdated() {
        this.updatedAt = LocalDateTime.now();
    }
}

