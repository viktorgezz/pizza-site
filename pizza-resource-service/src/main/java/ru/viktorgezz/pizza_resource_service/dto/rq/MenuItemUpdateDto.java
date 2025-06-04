package ru.viktorgezz.pizza_resource_service.dto.rq;

import org.springframework.web.multipart.MultipartFile;
import ru.viktorgezz.pizza_resource_service.util.Measure;

import java.math.BigDecimal;
import java.util.List;

public record MenuItemUpdateDto(
        Long id,
        String title,
        BigDecimal price,
        String description,
        MultipartFile image,
        List<MenuItemIngredientDto> ingredients
) {
    public record MenuItemIngredientDto(
            String ingredientTitle,
            BigDecimal quantity,
            Measure measure
    ) {}
} 