package ru.viktorgezz.pizza_resource_service.dto.rq;

import java.util.List;

public record OrderRequestDto(
    String orderType,
    List<CartItem> items
) {
    public record CartItem(
        String title,
        Integer quantity,
        Double price
    ) {}
} 