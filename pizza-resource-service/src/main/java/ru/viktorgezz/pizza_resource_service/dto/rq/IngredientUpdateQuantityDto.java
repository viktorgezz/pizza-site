package ru.viktorgezz.pizza_resource_service.dto.rq;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record IngredientUpdateQuantityDto(
        @NotBlank(message = "Название ингредиента обязательно")
        @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов")
        String title,

        @NotNull(message = "Количество обязательно")
        @Positive(message = "Количество должно быть больше 0")
        BigDecimal quantity
) {} 