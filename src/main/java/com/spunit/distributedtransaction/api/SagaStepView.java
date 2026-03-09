package com.spunit.distributedtransaction.api;

public record SagaStepView(
        String stepName,
        String status,
        String details
) {
}

