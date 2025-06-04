package ru.viktorgezz.pizza_resource_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.viktorgezz.pizza_resource_service.dto.rq.IngredientCreateDto;
import ru.viktorgezz.pizza_resource_service.dto.rq.IngredientUpdateInfoDto;
import ru.viktorgezz.pizza_resource_service.dto.rq.IngredientUpdateQuantityDto;
import ru.viktorgezz.pizza_resource_service.model.Ingredient;
import ru.viktorgezz.pizza_resource_service.util.Measure;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {
    private static final Logger log = LoggerFactory.getLogger(IngredientService.class);
    private final JdbcTemplate jdbc;

    private final RowMapper<Ingredient> ingredientMapper = (rs, rowNum) -> {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(rs.getLong("id"));
        ingredient.setTitle(rs.getString("title"));
        ingredient.setQuantity(rs.getBigDecimal("quantity"));
        ingredient.setMeasure(Measure.valueOf(rs.getString("measure")));
        return ingredient;
    };

    @Autowired
    public IngredientService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Ingredient> findByTitle(String title) {
        final String sql = """
                SELECT * FROM Ingredient WHERE title = ?
                """;
        try {
            Ingredient ingredient = jdbc.queryForObject(sql, ingredientMapper, title);
            log.info("Found ingredient with title: {}", title);
            return Optional.ofNullable(ingredient);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Ingredient not found with title: {}", title);
            return Optional.empty();
        }
    }

    public List<Ingredient> getAllIngredients() {
        final String sql = """
                SELECT * FROM Ingredient
                """;
        List<Ingredient> ingredients = jdbc.query(sql, ingredientMapper);
        log.info("Retrieved {} ingredients", ingredients.size());
        return ingredients;
    }

    @Transactional
    public void createIngredient(IngredientCreateDto ingredientCreateDto) {
        if (findByTitle(ingredientCreateDto.title()).isPresent()) {
            throw new IllegalArgumentException("Ингредиент с таким названием уже существует");
        }

        final String sql = """
                INSERT INTO Ingredient (title, quantity, measure)
                VALUES (?, 0, ?::measure)
                """;

        jdbc.update(sql, ingredientCreateDto.title(), ingredientCreateDto.measure().name());
        log.info("Created new ingredient: {} with measure: {}", ingredientCreateDto.title(), ingredientCreateDto.measure());
    }

    @Transactional
    public void updateQuantity(IngredientUpdateQuantityDto ingredientQuantityDto) {
        if (ingredientQuantityDto.quantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Количество ингредиента не может быть меньше или равно 0");
        }

        final String sql = """
                UPDATE Ingredient
                SET quantity = ?
                WHERE title = ?
                """;

        int updatedRows = jdbc.update(sql, ingredientQuantityDto.quantity(), ingredientQuantityDto.title());
        if (updatedRows == 0) {
            throw new IllegalArgumentException("Ингредиент с названием " + ingredientQuantityDto.title() + " не найден");
        }
        log.info("Updated quantity for ingredient: {} to {}", ingredientQuantityDto.title(), ingredientQuantityDto.quantity());
    }

    @Transactional
    public void updateTitleAndMeasure(IngredientUpdateInfoDto ingredientInfoDto) {
        if (!ingredientInfoDto.oldTitle().equals(ingredientInfoDto.newTitle()) && findByTitle(ingredientInfoDto.newTitle()).isPresent()) {
            throw new IllegalArgumentException("Ингредиент с названием " + ingredientInfoDto.newTitle() + " уже существует");
        }

        final String sql = """
                UPDATE Ingredient
                SET title = ?, measure = ?::measure
                WHERE title = ?
                """;

        int updatedRows = jdbc.update(sql, ingredientInfoDto.newTitle(), ingredientInfoDto.newMeasure().name(), ingredientInfoDto.oldTitle());
        if (updatedRows == 0) {
            throw new IllegalArgumentException("Ингредиент с названием " + ingredientInfoDto.oldTitle() + " не найден");
        }
        log.info("Updated ingredient {} to: title={}, measure={}", ingredientInfoDto.oldTitle(), ingredientInfoDto.newTitle(), ingredientInfoDto.newMeasure());
    }

    @Transactional
    public void deleteIngredient(String title) {
        final String sql = """
                DELETE FROM Ingredient WHERE title = ?
                """;

        int deletedRows = jdbc.update(sql, title);
        if (deletedRows == 0) {
            throw new IllegalArgumentException("Ингредиент с названием " + title + " не найден");
        }
        log.info("Deleted ingredient: {}", title);
    }
}
