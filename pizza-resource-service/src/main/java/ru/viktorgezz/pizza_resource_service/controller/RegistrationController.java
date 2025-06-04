package ru.viktorgezz.pizza_resource_service.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.viktorgezz.pizza_resource_service.dto.rq.RegisterCustomerDto;
import ru.viktorgezz.pizza_resource_service.dto.rq.RegisterEmployeeDto;
import ru.viktorgezz.pizza_resource_service.service.RegisterUserService;

@RestController
@RequestMapping("/signup")
public class RegistrationController {

    private static final String MESSAGE_SUCCESS_SIGNIN = "Успешная регистрация";
    
    private final RegisterUserService registerUserService;

    @Autowired
    public RegistrationController(RegisterUserService registerUserService) {
        this.registerUserService = registerUserService;
    }

    @PostMapping("/customer")
    public ResponseEntity<String> signin(@Valid @RequestBody RegisterCustomerDto registerDto) {
        registerUserService.save(registerDto);
        return ResponseEntity.ok(MESSAGE_SUCCESS_SIGNIN);
    }

    @PostMapping("/employee/manager")
    @PreAuthorize("hasRole('MAIN')")
    public ResponseEntity<String> hireManager(@Valid @RequestBody RegisterEmployeeDto registerDto) {

        return ResponseEntity.ok(MESSAGE_SUCCESS_SIGNIN);
    }

    @PostMapping("/employee/worker")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> hireWorker(@Valid @RequestBody RegisterEmployeeDto registerDto) {
        return ResponseEntity.ok(MESSAGE_SUCCESS_SIGNIN);
    }
}
