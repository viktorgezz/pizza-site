package ru.viktorgezz.auth_service.dto.rq;

public record RegisterRequest(
        String username,
        String password
) {
}
