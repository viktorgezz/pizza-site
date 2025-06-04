package ru.viktorgezz.pizza_resource_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.viktorgezz.pizza_resource_service.dto.rs.CustomerDto;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @GetMapping()
    public ResponseEntity<CustomerDto> getInfoCustomerBySession() {

        return ResponseEntity.ok(new CustomerDto("df", "sd"));
    }


    @GetMapping("/{id-customer}")
    @PreAuthorize("hasAnyRole('MAIN', 'MANAGER', 'COURIER')")
    public ResponseEntity<CustomerDto> getInfoCustomerById(
            @PathVariable("id-customer") Long id
    ) {

        return ResponseEntity.ok(new CustomerDto("df", "sd"));
    }


    @PutMapping()
    public ResponseEntity<String> changeInfoAboutCustomer() {

        return ResponseEntity.ok("Данные поменяны");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCustomer() {

        return ResponseEntity.ok("Пользователь удален");
    }

}
