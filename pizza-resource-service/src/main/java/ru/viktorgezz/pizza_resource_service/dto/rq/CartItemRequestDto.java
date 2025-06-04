package ru.viktorgezz.pizza_resource_service.dto.rq;

import java.math.BigDecimal;

public record CartItemRequestDto(
        String title,
        BigDecimal price
) {
}
