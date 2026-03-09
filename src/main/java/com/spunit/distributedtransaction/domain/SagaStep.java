package com.spunit.distributedtransaction.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "saga_step")
public class SagaStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String sagaId;

    @Column(nullable = false)
    private String stepName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStepStatus status;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(columnDefinition = "TEXT")
    private String details;

    public Long getId() {
        return id;
    }

    public String getSagaId() {
        return sagaId;
    }

    public void setSagaId(String sagaId) {
        this.sagaId = sagaId;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public SagaStepStatus getStatus() {
        return status;
    }

    public void setStatus(SagaStepStatus status) {
        this.status = status;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}

