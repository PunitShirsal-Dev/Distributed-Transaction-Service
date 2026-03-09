package com.spunit.distributedtransaction.service;

import com.spunit.distributedtransaction.api.SagaResponse;
import com.spunit.distributedtransaction.api.SagaStepView;
import com.spunit.distributedtransaction.api.StartSagaRequest;
import com.spunit.distributedtransaction.domain.SagaStatus;
import com.spunit.distributedtransaction.domain.SagaStep;
import com.spunit.distributedtransaction.domain.SagaStepStatus;
import com.spunit.distributedtransaction.domain.SagaTransaction;
import com.spunit.distributedtransaction.repo.SagaStepRepository;
import com.spunit.distributedtransaction.repo.SagaTransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SagaOrchestratorService {

    private final SagaTransactionRepository sagaTransactionRepository;
    private final SagaStepRepository sagaStepRepository;
    private final LocalTransactionSteps localTransactionSteps;

    public SagaOrchestratorService(SagaTransactionRepository sagaTransactionRepository,
                                   SagaStepRepository sagaStepRepository,
                                   LocalTransactionSteps localTransactionSteps) {
        this.sagaTransactionRepository = sagaTransactionRepository;
        this.sagaStepRepository = sagaStepRepository;
        this.localTransactionSteps = localTransactionSteps;
    }

    @Transactional
    public SagaResponse startSaga(StartSagaRequest request) {
        String sagaId = UUID.randomUUID().toString();

        SagaTransaction tx = new SagaTransaction();
        tx.setSagaId(sagaId);
        tx.setOrderId(request.orderId());
        tx.setStatus(SagaStatus.STARTED);
        tx.setCreatedAt(Instant.now());
        tx.setUpdatedAt(Instant.now());
        sagaTransactionRepository.save(tx);

        List<String> successfulSteps = new ArrayList<>();

        try {
            executeStep(sagaId, "RESERVE_INVENTORY", () -> localTransactionSteps.reserveInventory(request.failOnInventory()));
            successfulSteps.add("RESERVE_INVENTORY");

            executeStep(sagaId, "CHARGE_PAYMENT", () -> localTransactionSteps.chargePayment(request.failOnPayment()));
            successfulSteps.add("CHARGE_PAYMENT");

            executeStep(sagaId, "CREATE_SHIPMENT", () -> localTransactionSteps.createShipment(request.failOnShipping()));
            successfulSteps.add("CREATE_SHIPMENT");

            tx.setStatus(SagaStatus.COMPLETED);
            tx.setUpdatedAt(Instant.now());
            sagaTransactionRepository.save(tx);
            return mapToResponse(tx);
        } catch (Exception ex) {
            tx.setStatus(SagaStatus.COMPENSATING);
            tx.setFailureReason(ex.getMessage());
            tx.setUpdatedAt(Instant.now());
            sagaTransactionRepository.save(tx);

            compensate(sagaId, successfulSteps);

            tx.setStatus(SagaStatus.COMPENSATED);
            tx.setUpdatedAt(Instant.now());
            sagaTransactionRepository.save(tx);
            return mapToResponse(tx);
        }
    }

    public SagaResponse getSaga(String sagaId) {
        SagaTransaction tx = sagaTransactionRepository.findById(sagaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saga not found"));
        return mapToResponse(tx);
    }

    private void executeStep(String sagaId, String stepName, Runnable action) {
        SagaStep step = new SagaStep();
        step.setSagaId(sagaId);
        step.setStepName(stepName);
        step.setStatus(SagaStepStatus.PENDING);
        step.setUpdatedAt(Instant.now());
        sagaStepRepository.save(step);

        try {
            action.run();
            step.setStatus(SagaStepStatus.SUCCESS);
            step.setDetails("Completed");
        } catch (Exception e) {
            step.setStatus(SagaStepStatus.FAILED);
            step.setDetails(e.getMessage());
            sagaStepRepository.save(step);
            throw e;
        }

        step.setUpdatedAt(Instant.now());
        sagaStepRepository.save(step);
    }

    private void compensate(String sagaId, List<String> successfulSteps) {
        for (int i = successfulSteps.size() - 1; i >= 0; i--) {
            String step = successfulSteps.get(i);
            switch (step) {
                case "CREATE_SHIPMENT" -> {
                    localTransactionSteps.compensateShipment();
                    markCompensated(sagaId, step, "Compensated: shipment cancelled");
                }
                case "CHARGE_PAYMENT" -> {
                    localTransactionSteps.compensatePayment();
                    markCompensated(sagaId, step, "Compensated: payment refunded");
                }
                case "RESERVE_INVENTORY" -> {
                    localTransactionSteps.compensateInventory();
                    markCompensated(sagaId, step, "Compensated: inventory released");
                }
                default -> {
                }
            }
        }
    }

    private void markCompensated(String sagaId, String stepName, String details) {
        SagaStep step = new SagaStep();
        step.setSagaId(sagaId);
        step.setStepName(stepName + "_COMPENSATION");
        step.setStatus(SagaStepStatus.COMPENSATED);
        step.setDetails(details);
        step.setUpdatedAt(Instant.now());
        sagaStepRepository.save(step);
    }

    private SagaResponse mapToResponse(SagaTransaction tx) {
        List<SagaStepView> views = sagaStepRepository.findBySagaIdOrderByIdAsc(tx.getSagaId())
                .stream()
                .map(s -> new SagaStepView(s.getStepName(), s.getStatus().name(), s.getDetails()))
                .toList();

        return new SagaResponse(
                tx.getSagaId(),
                tx.getOrderId(),
                tx.getStatus().name(),
                tx.getFailureReason(),
                views
        );
    }
}

