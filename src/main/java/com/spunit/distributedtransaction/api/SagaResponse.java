package com.spunit.distributedtransaction.api;

import java.util.List;

public record SagaResponse(
        String sagaId,
        String orderId,
        String status,
        String failureReason,
        List<SagaStepView> steps
) {
}

