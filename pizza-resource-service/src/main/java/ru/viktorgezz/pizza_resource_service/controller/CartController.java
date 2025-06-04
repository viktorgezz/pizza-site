package ru.viktorgezz.pizza_resource_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.viktorgezz.pizza_resource_service.dto.rq.CartItemRequestDto;
import ru.viktorgezz.pizza_resource_service.model.CartItem;
import ru.viktorgezz.pizza_resource_service.service.CartService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping()
    public ResponseEntity<List<CartItem>> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotalPrice() {
        return ResponseEntity.ok(Map.of("total", cartService.getTotalPrice()));
    }

    @PostMapping("/add")
    public ResponseEntity<List<CartItem>> addToCart(
            @RequestBody CartItemRequestDto cartDto
    ) {
        cartService.addToCart(cartDto.title(), cartDto.price());
        return ResponseEntity.ok(cartService.getCart());
    }

    @PostMapping("/decrease")
    public ResponseEntity<List<CartItem>> decreaseQuantity(
            @RequestBody CartItemRequestDto cartDto
    ) {
        cartService.decreaseQuantity(cartDto.title());
        return ResponseEntity.ok(cartService.getCart());
    }

    @DeleteMapping()
    public ResponseEntity<List<CartItem>> removeFromCart(
            @RequestBody CartItemRequestDto cartDto) {
        cartService.removeFromCart(cartDto.title());
        return ResponseEntity.ok(cartService.getCart());
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}
