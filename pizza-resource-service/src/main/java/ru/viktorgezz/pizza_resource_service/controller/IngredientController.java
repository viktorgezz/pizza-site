package ru.viktorgezz.pizza_resource_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.viktorgezz.pizza_resource_service.dto.rq.IngredientCreateDto;
import ru.viktorgezz.pizza_resource_service.dto.rq.IngredientUpdateInfoDto;
import ru.viktorgezz.pizza_resource_service.dto.rq.IngredientUpdateQuantityDto;
import ru.viktorgezz.pizza_resource_service.model.Ingredient;
import ru.viktorgezz.pizza_resource_service.service.IngredientService;

import java.util.List;

@RestController
@RequestMapping("/ingredient")
public class IngredientController {

    private final IngredientService ingredientService;

    @Autowired
    public IngredientController(
            IngredientService ingredientService
    ) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/{title}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Ingredient> getIngredient(@PathVariable String title) {
        return ResponseEntity.ok(
                ingredientService
                        .findByTitle(title)
                        .orElseThrow());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<Ingredient>> getIngredients() {
        return ResponseEntity.ok(ingredientService.getAllIngredients());
    }

    @PostMapping()
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> addIngredient(
            @RequestBody IngredientCreateDto ingrDto
    ) {
        ingredientService.createIngredient(ingrDto);
        return ResponseEntity.ok("Добавлен новый ингредиент");
    }

    @PatchMapping()
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> changeQuantityIngredient(
            @RequestBody IngredientUpdateQuantityDto ingrDto
    ) {
        ingredientService.updateQuantity(ingrDto);
        return ResponseEntity.ok("Изменено количество ингредиентов");
    }

    @PutMapping()
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> updateDataIngredient(
            @RequestBody IngredientUpdateInfoDto ingrDto
    ) {
        ingredientService.updateTitleAndMeasure(ingrDto);
        return ResponseEntity.ok("Обновлен ингредиент");
    }

    @DeleteMapping("/{title}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> writeOffIngredient(
            @PathVariable String title
    ) {
        ingredientService.deleteIngredient(title);
        return ResponseEntity.ok("Списан ингредиент");
    }

}
