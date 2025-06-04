package ru.viktorgezz.pizza_resource_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.viktorgezz.pizza_resource_service.dto.rq.EmployeeUpdateDto;
import ru.viktorgezz.pizza_resource_service.dto.rq.RegisterEmployeeDto;
import ru.viktorgezz.pizza_resource_service.model.Employee;
import ru.viktorgezz.pizza_resource_service.service.EmployeeService;
import ru.viktorgezz.pizza_resource_service.util.Role;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(
            EmployeeService employeeService
    ) {
        this.employeeService = employeeService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('MAIN', 'MANAGER')")
    public ResponseEntity<List<Employee>> getEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAnyRole('MAIN', 'MANAGER', 'WORKER', COURIER)")
    public ResponseEntity<Employee> findInfoByUsername(@PathVariable String username) {
        return ResponseEntity.ok(
                employeeService
                        .findEmployeeByUsername(username)
                        .orElseThrow());
    }

    @PatchMapping()
    @PreAuthorize("hasAnyRole('MAIN', 'MANAGER')")
    public ResponseEntity<String> updateInfo(@RequestBody EmployeeUpdateDto empl) {
        employeeService.updateEmployee(empl);
        return ResponseEntity.ok("Успешно изменён");
    }

    @GetMapping("/all/{role}")
    @PreAuthorize("hasAnyRole('MAIN', 'MANAGER')")
    public ResponseEntity<List<Employee>> getEmployeesByRole(@PathVariable String role) {
        return ResponseEntity.ok(employeeService.getEmployeesByRole(Role.valueOf(role)));
    }

    @PostMapping("/{role}")
    @PreAuthorize("hasAnyRole('MAIN', 'MANAGER')")
    public ResponseEntity<String> hireEmployee(
            @RequestBody RegisterEmployeeDto newEmployee,
            @PathVariable String role) {
        employeeService.hireEmployee(newEmployee, Role.valueOf(role));

        return ResponseEntity.ok("Сотрудник нанят " + role);
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasAnyRole('MAIN', 'MANAGER')")
    public ResponseEntity<String> fireEmployee(@PathVariable String username) {
        employeeService.fireEmployee(username);
        return ResponseEntity.ok("Сотрудник уволен");
    }
}
