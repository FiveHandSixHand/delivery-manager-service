package com.fhsh.daitda.deliverymanager.infrastructure.external.dto;

public record ApiResponse<T>(
        int status,
        String message,
        T data
) { }
