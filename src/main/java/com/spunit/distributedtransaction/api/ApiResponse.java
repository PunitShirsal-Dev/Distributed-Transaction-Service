package com.spunit.distributedtransaction.api;

public record ApiResponse<T>(
        String message,
        T data,
        int statusCode
) {
}

