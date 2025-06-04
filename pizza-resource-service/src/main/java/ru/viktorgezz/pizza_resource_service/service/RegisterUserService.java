package ru.viktorgezz.pizza_resource_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.viktorgezz.pizza_resource_service.dto.rq.RegisterCustomerDto;
import ru.viktorgezz.pizza_resource_service.model.User;
import ru.viktorgezz.pizza_resource_service.util.Role;

import java.util.Optional;

@Service
public class RegisterUserService {

    private static final Logger log = LoggerFactory.getLogger(RegisterUserService.class);
    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegisterUserService(
        JdbcTemplate jdbc, 
        PasswordEncoder passwordEncoder
    ) {
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        final String sql = "SELECT * FROM _user WHERE username = ?";
        return jdbc.query(sql, (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getBoolean("enabled"),
                Role.valueOf(rs.getString("role").toUpperCase()),
                rs.getString("email")
        ), username).stream().findFirst();
    }


    @Transactional
    public void save(RegisterCustomerDto user) {
        validateNewUser(user);

        final String sqlUser = """
                INSERT INTO _user (username, password, email, enabled, role)
                VALUES (?, ?, ?, true, 'USER'::role)
                RETURNING id
                """;

        Long userId = jdbc.queryForObject(sqlUser, Long.class,
                user.username(),
                passwordEncoder.encode(user.password()),
                user.email());

        if (userId == null) {
            throw new RuntimeException("Не удалось получить ID пользователя после вставки");
        }

        final String sqlCustomer = """
                INSERT INTO Customer (address, phone, user_id)
                VALUES (?, ?, ?)
                """;

        jdbc.update(sqlCustomer,
                user.address(),
                user.phone(),
                userId);
    }

    private void validateNewUser(RegisterCustomerDto user) {
        if (findByUsername(user.username()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }
        if (isEmailExists(user.email())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
        if (isPhoneExists(user.phone())) {
            throw new IllegalArgumentException("Пользователь с таким номером телефона уже существует");
        }
    }

    private boolean isEmailExists(String email) {
        final String sql = "SELECT COUNT(*) FROM _user WHERE email = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    private boolean isPhoneExists(String phone) {
        final String sql = "SELECT COUNT(*) FROM Customer WHERE phone = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, phone);
        return count != null && count > 0;
    }
}
