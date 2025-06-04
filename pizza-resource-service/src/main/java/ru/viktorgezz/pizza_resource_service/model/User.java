package ru.viktorgezz.pizza_resource_service.model;

import jakarta.validation.constraints.*;
import ru.viktorgezz.pizza_resource_service.util.Role;

import java.util.Objects;

public class User {

    @NotNull(message = "ID не может быть пустым")
    private Long idUser;

    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Имя пользователя может содержать только буквы, цифры и знак подчеркивания")
    private String username;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, max = 255, message = "Пароль должен быть от 8 до 255 символов")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
            message = "Пароль должен содержать минимум одну заглавную букву, одну строчную букву, одну цифру и один специальный символ")
    private String password;

    @NotNull(message = "Статус активности обязателен")
    private Boolean enabled;

    @NotNull(message = "Роль обязательна")
    private Role role;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Size(max = 255, message = "Email не может быть длиннее 255 символов")
    private String email;

    public User() {
    }

    public User(Long idUser,
                String username,
                String password,
                Boolean enabled,
                Role role,
                String email) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(idUser, user.idUser) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(enabled, user.enabled) && role == user.role && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, username, password, enabled, role, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + idUser +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", role=" + role +
                ", email='" + email + '\'' +
                '}';
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
