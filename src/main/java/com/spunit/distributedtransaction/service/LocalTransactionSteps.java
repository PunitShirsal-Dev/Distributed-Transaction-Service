package com.spunit.distributedtransaction.service;

import org.springframework.stereotype.Component;

@Component
public class LocalTransactionSteps {

    public void reserveInventory(boolean shouldFail) {
        if (shouldFail) {
            throw new IllegalStateException("Inventory reservation failed");
        }
    }

    public void chargePayment(boolean shouldFail) {
        if (shouldFail) {
            throw new IllegalStateException("Payment charge failed");
        }
    }

    public void createShipment(boolean shouldFail) {
        if (shouldFail) {
            throw new IllegalStateException("Shipment creation failed");
        }
    }

    public void compensateInventory() {
        // Compensation hook: release reserved inventory
    }

    public void compensatePayment() {
        // Compensation hook: refund payment
    }

    public void compensateShipment() {
        // Compensation hook: cancel shipment
    }
}

