package com.spunit.distributedtransaction.controller;

import com.spunit.distributedtransaction.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Map<String, Object>> health() {
        return new ApiResponse<>(
                "Service is healthy",
                Map.of(
                        "service", "Distributed-Transaction-Service",
                        "status", "UP",
                        "timestamp", Instant.now().toString()
                ),
                HttpStatus.OK.value()
        );
    }
}

