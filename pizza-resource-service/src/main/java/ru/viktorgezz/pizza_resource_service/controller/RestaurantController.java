package ru.viktorgezz.pizza_resource_service.controller;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.viktorgezz.pizza_resource_service.dto.rq.RestaurantUpdateDto;
import ru.viktorgezz.pizza_resource_service.dto.rs.RestaurantDto;
import ru.viktorgezz.pizza_resource_service.model.Restaurant;
import ru.viktorgezz.pizza_resource_service.service.RestaurantService;
import ru.viktorgezz.pizza_resource_service.util.StatusRestaurant;

import java.sql.Time;
import java.util.List;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final ModelMapper modelMapper;

    @Autowired
    public RestaurantController(
            RestaurantService restaurantService,
            ModelMapper modelMapper
    ) {
        this.restaurantService = restaurantService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<RestaurantDto>> getAllRestaurants() {
        return ResponseEntity.ok(
                restaurantService.findAll().stream()
                        .map(this::convertToDto)
                        .toList()
        );
    }

    @GetMapping()
    public ResponseEntity<RestaurantDto> getRestaurant() {
        return ResponseEntity.ok(
                restaurantService.findAll().stream()
                        .map(this::convertToDto)
                        .findFirst().orElseThrow()
        );
    }

    @GetMapping("/{address}")
    public ResponseEntity<RestaurantDto> getRestaurantByAddress(@PathVariable String address) {
        return restaurantService.findByAddress(address)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('MAIN')")
    public ResponseEntity<String> createRestaurant(
            @Valid @RequestBody RestaurantUpdateDto restaurantDto
    ) {
        restaurantService.create(restaurantDto);
        return ResponseEntity.ok("Рестаран создан");
    }

    @PutMapping("/{address}")
    @PreAuthorize("hasRole('MAIN')")
    public ResponseEntity<String> updateRestaurant(
            @PathVariable String address,
            @Valid @RequestBody RestaurantUpdateDto restaurantDto
    ) {
        restaurantService.update(address, restaurantDto);
        return ResponseEntity.ok("Данные ресторана обновлены");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MAIN')")
    public ResponseEntity<String> deleteRestaurant(@PathVariable Long id) {
        restaurantService.delete(id);
        return ResponseEntity.ok("Ресторан удален");
    }

    @PatchMapping("/{address}/status")
    @PreAuthorize("hasAnyRole('ROLE_MAIN', 'ROLE_MANAGER')")
    public ResponseEntity<String> updateRestaurantStatus(
            @PathVariable String address,
            @RequestParam StatusRestaurant status
    ) {
        restaurantService.updateStatus(address, status);
        return ResponseEntity.ok("Статус обновлен");
    }

    @PatchMapping("/{address}/working-hours")
    @PreAuthorize("hasAnyRole('MAIN', 'MANAGER')")
    public ResponseEntity<String> updateWorkingHours(
            @PathVariable String address,
            @RequestParam(required = false) Time openingTime,
            @RequestParam(required = false) Time closingTime
    ) {
        restaurantService.updateWorkingHours(address, openingTime, closingTime);
        return ResponseEntity.ok("Время работы обновлено");
    }

    private RestaurantDto convertToDto(Restaurant restaurant) {
        return modelMapper.map(restaurant, RestaurantDto.class);
    }
}
