package ru.viktorgezz.pizza_resource_service.dto.rs;

import ru.viktorgezz.pizza_resource_service.util.StatusOrder;
import ru.viktorgezz.pizza_resource_service.util.TypeOrder;

import java.sql.Timestamp;
import java.util.List;

public record OrderDto(
        Long id,
        Long customerId,
        Long restaurantId,
        Long courierId,
        StatusOrder status,
        TypeOrder orderType,
        Timestamp date,
        List<String> menuItems
    ) {}
