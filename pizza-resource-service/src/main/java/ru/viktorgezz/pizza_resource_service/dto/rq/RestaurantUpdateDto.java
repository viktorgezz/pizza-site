package ru.viktorgezz.pizza_resource_service.dto.rq;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.viktorgezz.pizza_resource_service.util.StatusRestaurant;

import java.sql.Time;

public record RestaurantUpdateDto(
    @NotBlank(message = "Адрес обязателен")
    @Size(max = 255, message = "Адрес не может быть длиннее 255 символов")
    String address,

    StatusRestaurant status,
    Time openingTime,
    Time closingTime
) {} 