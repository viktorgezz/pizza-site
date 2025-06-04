package ru.viktorgezz.pizza_resource_service.dto.rq;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.viktorgezz.pizza_resource_service.util.Measure;

public record IngredientCreateDto(
        @NotBlank(message = "Название ингредиента обязательно")
        @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов")
        String title,

        @NotNull(message = "Единица измерения обязательна")
        Measure measure
) {} 