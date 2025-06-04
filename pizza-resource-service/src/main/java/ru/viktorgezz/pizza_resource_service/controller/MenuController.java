package ru.viktorgezz.pizza_resource_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.viktorgezz.pizza_resource_service.dto.rq.MenuItemCreateDto;
import ru.viktorgezz.pizza_resource_service.dto.rq.MenuItemUpdateDto;
import ru.viktorgezz.pizza_resource_service.dto.rs.MenuItemResponseDto;
import ru.viktorgezz.pizza_resource_service.model.MenuItem;
import ru.viktorgezz.pizza_resource_service.service.MenuItemService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private final MenuItemService menuItemService;

    @Autowired
    public MenuController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping("/{title}")
    public ResponseEntity<MenuItem> getMenuItemByTitle(@PathVariable String title) {
        return ResponseEntity.ok(
                menuItemService
                        .findByTitle(title)
                        .orElseThrow(() -> new IllegalArgumentException("Пункт меню с названием " + title + " не найден"))
        );
    }

    @GetMapping("/ingredients/{id}")
    public ResponseEntity<List<MenuItemUpdateDto.MenuItemIngredientDto>> getMenuItemIngredients(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.getMenuItemIngredients(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemService.getAllMenuItems());
    }

    @GetMapping("/with-images")
    public ResponseEntity<List<MenuItemResponseDto>> getAllMenuItemsWithImages() {
        return ResponseEntity.ok(menuItemService.getAllMenuItemsWithImages());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> createMenuItem(
            @RequestParam("idRestaurant") Long idRestaurant,
            @RequestParam("title") String title,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description,
            @RequestParam("image") MultipartFile image,
            @RequestParam("ingredients") String ingredientsJson
    ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<MenuItemCreateDto.MenuItemIngredientDto> ingredients = mapper.readValue(
            ingredientsJson,
            mapper.getTypeFactory().constructCollectionType(
                List.class,
                MenuItemCreateDto.MenuItemIngredientDto.class
            )
        );
        
        MenuItemCreateDto createDto = new MenuItemCreateDto(
            idRestaurant,
            title,
            price,
            description,
            image,
            ingredients
        );
        
        menuItemService.createMenuItem(createDto);
        return ResponseEntity.ok("Добавлен новый пункт меню");
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> updateMenuItem(
            @RequestParam("id") Long id,
            @RequestParam("title") String title,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("ingredients") String ingredientsJson
    ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<MenuItemUpdateDto.MenuItemIngredientDto> ingredients = mapper.readValue(
            ingredientsJson,
            mapper.getTypeFactory().constructCollectionType(
                List.class,
                MenuItemUpdateDto.MenuItemIngredientDto.class
            )
        );
        
        MenuItemUpdateDto updateDto = new MenuItemUpdateDto(
            id,
            title,
            price,
            description,
            image,
            ingredients
        );
        
        menuItemService.updateMenuItem(updateDto);
        return ResponseEntity.ok("Обновлен пункт меню");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> deleteMenuItem(
            @PathVariable Long id
    ) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.ok("Удален пункт меню");
    }
}
