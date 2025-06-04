package ru.viktorgezz.pizza_resource_service.dto.rs;

import java.math.BigDecimal;
import java.util.List;

public record MenuItemResponseDto(
    String title,
    BigDecimal price,
    String description,
    List<IngredientDto> ingredients,
    String imageBase64
) {
    public record IngredientDto(
        String title,
        BigDecimal quantity,
        String measure
    ) {}
} 