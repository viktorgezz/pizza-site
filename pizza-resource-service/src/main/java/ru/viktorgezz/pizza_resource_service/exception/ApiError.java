package ru.viktorgezz.pizza_resource_service.exception;

import java.time.LocalDateTime;

public record ApiError(
        String message,
        String error,
        LocalDateTime timestamp
) {
    public static ApiError of(String message, String error) {
        return new ApiError(message, error, LocalDateTime.now());
    }
} 