package ru.viktorgezz.auth_service.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.viktorgezz.auth_service.model.User;
import ru.viktorgezz.auth_service.util.Role;

import java.util.Optional;

@Repository
public class UserRepo {

    private final JdbcTemplate jdbc;

    @Autowired
    public UserRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<User> findByUsername(String username) {
        final String sql = "SELECT * FROM _user WHERE username = ?";
        return jdbc.query(sql, (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getBoolean("enabled"),
                Role.valueOf(rs.getString("role").toUpperCase())
        ), username).stream().findFirst();
    }

    public void save(User user) {
        final String sql = "INSERT INTO _user (username, password) VALUES (?, ?)";
        jdbc.update(sql,
                user.getUsername(), user.getPassword());
    }

}
