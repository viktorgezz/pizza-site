package ru.viktorgezz.pizza_resource_service.service;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.viktorgezz.pizza_resource_service.dto.rq.RestaurantUpdateDto;
import ru.viktorgezz.pizza_resource_service.model.Restaurant;
import ru.viktorgezz.pizza_resource_service.util.StatusRestaurant;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

    private static final Logger log = LoggerFactory.getLogger(RestaurantService.class);

    private final JdbcTemplate jdbc;

    private final RowMapper<Restaurant> restaurantMapper = (rs, rowNum) -> new Restaurant(
            rs.getLong("id"),
            rs.getString("address"),
            StatusRestaurant.valueOf(rs.getString("status").toUpperCase()),
            rs.getTime("opening_time"),
            rs.getTime("closing_time")
    );

    @Autowired
    public RestaurantService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Restaurant> findAll() {
        final String sql = "SELECT * FROM Restaurant";
        return jdbc.query(sql, restaurantMapper);
    }

    public Optional<Restaurant> findByAddress(String address) {
        final String sql = "SELECT * FROM Restaurant WHERE address = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, restaurantMapper, address));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void create(RestaurantUpdateDto restaurantDto) {
        if (findByAddress(restaurantDto.address()).isPresent()) {
            throw new IllegalArgumentException("Ресторан с таким адресом уже существует");
        }

        final String sql = """
                INSERT INTO Restaurant (address, status, opening_time, closing_time)
                VALUES (?, ?::status_restaurant, ?, ?)
                RETURNING id
                """;

        Long id = jdbc.queryForObject(sql, Long.class,
                restaurantDto.address(),
                restaurantDto.status().name(),
                restaurantDto.openingTime(),
                restaurantDto.closingTime()
        );

        if (id == null) {
            throw new RuntimeException("Не удалось создать ресторан");
        }
    }

    public void update(String address, RestaurantUpdateDto restaurantDto) {
        Restaurant existingRestaurant = findByAddress(address)
                .orElseThrow(() -> new IllegalArgumentException("Ресторан с адресом " + address + " не найден"));

        final String sql = """
                UPDATE Restaurant 
                SET address = ?, 
                    status = ?::status_restaurant, 
                    opening_time = ?, 
                    closing_time = ?
                WHERE id = ?
                """;

        jdbc.update(sql,
                restaurantDto.address(),
                restaurantDto.status().name(),
                restaurantDto.openingTime(),
                restaurantDto.closingTime(),
                existingRestaurant.getId()
        );
    }

    public void delete(Long id) {
        if (!exists(id)) {
            throw new IllegalArgumentException("Ресторан с ID " + id + " не найден");
        }

        final String sql = "DELETE FROM Restaurant WHERE id = ?";
        jdbc.update(sql, id);
    }

    public void updateStatus(String address, StatusRestaurant status) {
        Restaurant restaurant = findByAddress(address)
                .orElseThrow(() -> new IllegalArgumentException("Ресторан с адресом " + address + " не найден"));

        final String sql = "UPDATE Restaurant SET status = ?::status_restaurant WHERE id = ?";
        jdbc.update(sql, status.name(), restaurant.getId());
    }

    public void updateWorkingHours(String address, Time openingTime, Time closingTime) {
        Restaurant restaurant = findByAddress(address)
                .orElseThrow(() -> new IllegalArgumentException("Ресторан с адресом " + address + " не найден"));

        final String sql = "UPDATE Restaurant SET opening_time = ?, closing_time = ? WHERE id = ?";
        jdbc.update(sql, openingTime, closingTime, restaurant.getId());
    }

    private boolean exists(Long id) {
        final String sql = "SELECT COUNT(*) FROM Restaurant WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
