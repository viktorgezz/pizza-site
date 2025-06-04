package ru.viktorgezz.pizza_resource_service.dto.rq;

import jakarta.validation.constraints.*;

public record RegisterEmployeeDto(
        @NotBlank(message = "Имя пользователя обязательно")
        @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Имя пользователя может содержать только буквы, цифры и знак подчеркивания")
        String username,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 8, max = 255, message = "Пароль должен быть от 8 до 255 символов")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
                message = "Пароль должен содержать минимум одну заглавную букву, одну строчную букву, одну цифру и один специальный символ")
        String password,

        @NotBlank(message = "Описание должности обязательно")
        @Size(max = 255, message = "Описание должности не может быть длиннее 255 символов")
        String jobDescription,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        @Size(max = 255, message = "Email не может быть длиннее 255 символов")
        String email
) {
}
