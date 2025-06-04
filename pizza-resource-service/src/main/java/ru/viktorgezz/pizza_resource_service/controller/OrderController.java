package ru.viktorgezz.pizza_resource_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.viktorgezz.pizza_resource_service.dto.rs.OrderDto;
import ru.viktorgezz.pizza_resource_service.dto.rq.OrderRequestDto;
import ru.viktorgezz.pizza_resource_service.service.OrderService;
import ru.viktorgezz.pizza_resource_service.util.StatusOrder;
import ru.viktorgezz.pizza_resource_service.util.TypeOrder;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

//    @PreAuthorize("hasAnyRole('MAIN', 'MANAGER', 'COURIER')")
    @PostMapping("/checkout")
    public ResponseEntity<Void> checkout(@RequestBody OrderRequestDto orderRequest) {
        orderService.createOrder(orderRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderDto>> getUserOrders() {
        return ResponseEntity.ok(orderService.getUserOrders());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('MANAGER', 'COURIER')")
    public ResponseEntity<List<OrderDto>> getOrdersByStatusAndType(
            @RequestParam StatusOrder status,
            @RequestParam TypeOrder type) {
        return ResponseEntity.ok(orderService.getOrdersByStatusAndType(status, type));
    }

    @PatchMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'COURIER', 'WORKER')")
    public ResponseEntity<Void> updateOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false) StatusOrder status,
            @RequestParam(required = false) TypeOrder type) {
        
        // Если не указаны новые значения, получаем текущие
        if (status == null || type == null) {
            OrderDto currentOrder = orderService.getOrderById(orderId);
            status = status != null ? status : currentOrder.status();
            type = type != null ? type : currentOrder.orderType();
        }
        
        orderService.updateOrder(orderId, status, type);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('MANAGER', 'COURIER', 'WORKER')")
    public ResponseEntity<List<OrderDto>> getOrdersByStatus(
            @PathVariable StatusOrder status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }
}
