package com.spunit.distributedtransaction.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record StartSagaRequest(
        @NotBlank String orderId,
        @NotBlank String customerId,
        @NotBlank String productCode,
        @Min(1) int quantity,
        @DecimalMin("0.01") BigDecimal amount,
        boolean failOnInventory,
        boolean failOnPayment,
        boolean failOnShipping
) {
}

