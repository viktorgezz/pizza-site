package ru.viktorgezz.pizza_resource_service.dto.rs;

import java.math.BigDecimal;

public record MenuItemDto(
        Long id,
        String title,
        BigDecimal price,
        String description,
        String imageUrl
) {
}
