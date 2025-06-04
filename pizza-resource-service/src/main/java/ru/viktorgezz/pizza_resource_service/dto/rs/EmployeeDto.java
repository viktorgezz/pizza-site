package ru.viktorgezz.pizza_resource_service.dto.rs;

public record EmployeeDto (
        Long id_employee,
        String username,
        String role,
        String jobDescription,
        String email,
        String addressRestaurant
) {
}
