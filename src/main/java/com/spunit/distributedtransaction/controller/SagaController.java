package com.spunit.distributedtransaction.controller;

import com.spunit.distributedtransaction.api.ApiResponse;
import com.spunit.distributedtransaction.api.SagaResponse;
import com.spunit.distributedtransaction.api.StartSagaRequest;
import com.spunit.distributedtransaction.service.SagaOrchestratorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sagas")
public class SagaController {

    private final SagaOrchestratorService sagaOrchestratorService;

    public SagaController(SagaOrchestratorService sagaOrchestratorService) {
        this.sagaOrchestratorService = sagaOrchestratorService;
    }

    @PostMapping("/order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<SagaResponse> startOrderSaga(@Valid @RequestBody StartSagaRequest request) {
        SagaResponse response = sagaOrchestratorService.startSaga(request);
        return new ApiResponse<>("Saga executed", response, HttpStatus.ACCEPTED.value());
    }

    @GetMapping("/{sagaId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SagaResponse> getSaga(@PathVariable String sagaId) {
        SagaResponse response = sagaOrchestratorService.getSaga(sagaId);
        return new ApiResponse<>("Saga details", response, HttpStatus.OK.value());
    }
}

