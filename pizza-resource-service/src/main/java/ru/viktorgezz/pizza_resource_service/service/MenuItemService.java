package ru.viktorgezz.pizza_resource_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.viktorgezz.pizza_resource_service.dto.rq.MenuItemCreateDto;
import ru.viktorgezz.pizza_resource_service.dto.rq.MenuItemUpdateDto;
import ru.viktorgezz.pizza_resource_service.dto.rs.MenuItemResponseDto;
import ru.viktorgezz.pizza_resource_service.model.MenuItem;
import ru.viktorgezz.pizza_resource_service.model.MenuItemIngredient;
import ru.viktorgezz.pizza_resource_service.util.Measure;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.Base64;
import java.util.stream.Collectors;

@Service
public class MenuItemService {

    private static final Logger log = LoggerFactory.getLogger(MenuItemService.class);

    private final JdbcTemplate jdbc;
    private final S3Service s3Service;

    @Autowired
    public MenuItemService(
            JdbcTemplate jdbc,
            S3Service s3Service) {
        this.jdbc = jdbc;
        this.s3Service = s3Service;
    }

    private final RowMapper<MenuItem> menuItemMapper = (rs, rowNum) -> {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(rs.getLong("id"));
        menuItem.setIdRestaurant(rs.getLong("id_restaurant"));
        menuItem.setTitle(rs.getString("title"));
        menuItem.setPrice(rs.getBigDecimal("price"));
        menuItem.setDescription(rs.getString("description"));
        menuItem.setImageUrl(rs.getString("image_url"));
        return menuItem;
    };

    private final RowMapper<MenuItemIngredient> menuItemIngredientMapper = (rs, rowNum) -> {
        MenuItemIngredient ingredient = new MenuItemIngredient();
        ingredient.setId(rs.getLong("id"));
        ingredient.setIdMenuItem(rs.getLong("id_menu_item"));
        ingredient.setIdIngredient(rs.getLong("id_ingredient"));
        ingredient.setQuantity(rs.getBigDecimal("quantity"));
        ingredient.setMeasure(Measure.valueOf(rs.getString("measure")));
        return ingredient;
    };

    public List<MenuItem> getAllMenuItems() {
        final String sql = """
                SELECT * FROM Menu_item
                """;
        List<MenuItem> menuItems = jdbc.query(sql, menuItemMapper);

        for (MenuItem menuItem : menuItems) {
            loadMenuItemIngredients(menuItem);
        }

        log.info("Retrieved {} menu items", menuItems.size());
        return menuItems;
    }

    private void loadMenuItemIngredients(MenuItem menuItem) {
        final String sql = """
                SELECT mi.*, i.title as ingredient_title 
                FROM Menu_item_ingredient mi
                JOIN Ingredient i ON mi.id_ingredient = i.id
                WHERE mi.id_menu_item = ?
                """;
        List<MenuItemIngredient> ingredients = jdbc.query(sql, menuItemIngredientMapper, menuItem.getId());
        log.debug("Loaded {} ingredients for menu item {}", ingredients.size(), menuItem.getId());
    }

    @Transactional
    public void createMenuItem(MenuItemCreateDto createDto) throws IOException {
        String imageUrl = s3Service.uploadFile(createDto.image());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String menuItemSql = """
                INSERT INTO Menu_item (id_restaurant, title, price, description, image_url)
                VALUES (?, ?, ?, ?, ?) RETURNING id
                """;

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(menuItemSql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, createDto.idRestaurant());
            ps.setString(2, createDto.title());
            ps.setBigDecimal(3, createDto.price());
            ps.setString(4, createDto.description());
            ps.setString(5, imageUrl);
            return ps;
        }, keyHolder);

        Long menuItemId = ((Number) keyHolder.getKeys().get("id")).longValue();

        for (MenuItemCreateDto.MenuItemIngredientDto ingredientDto : createDto.ingredients()) {
            Optional<Long> ingredientId = getIngredientIdByTitle(ingredientDto.ingredientTitle());

            if (ingredientId.isEmpty()) {
                throw new IllegalArgumentException("Ingredient not found: " + ingredientDto.ingredientTitle());
            }

            final String ingredientSql = """
                    INSERT INTO Menu_item_ingredient (id_ingredient, id_menu_item, quantity, measure)
                    VALUES (?, ?, ?, ?::measure)
                    """;

            jdbc.update(ingredientSql,
                    ingredientId.get(),
                    menuItemId,
                    ingredientDto.quantity(),
                    ingredientDto.measure().name());
        }

        log.info("Created new menu item with ID: {}", menuItemId);
    }

    @Transactional
    public void updateMenuItem(MenuItemUpdateDto updateDto) throws IOException {
        if (!menuItemExists(updateDto.id())) {
            throw new IllegalArgumentException("Menu item not found with ID: " + updateDto.id());
        }

        String imageUrl = null;
        if (updateDto.image() != null && !updateDto.image().isEmpty()) {
            String oldImageUrl = getMenuItemImageUrl(updateDto.id());
            if (oldImageUrl != null) {
                String oldFileName = oldImageUrl.substring(oldImageUrl.lastIndexOf('/') + 1);
                s3Service.deleteFile(oldFileName);
            }

            imageUrl = s3Service.uploadFile(updateDto.image());
        }

        final String updateMenuItemSql = """
                UPDATE Menu_item
                SET title = ?,
                    price = ?,
                    description = ?
                """ + (imageUrl != null ? ", image_url = ?" : "") + """
                WHERE id = ?
                """;

        if (imageUrl != null) {
            jdbc.update(updateMenuItemSql,
                    updateDto.title(),
                    updateDto.price(),
                    updateDto.description(),
                    imageUrl,
                    updateDto.id());
        } else {
            jdbc.update(updateMenuItemSql,
                    updateDto.title(),
                    updateDto.price(),
                    updateDto.description(),
                    updateDto.id());
        }

        jdbc.update("DELETE FROM Menu_item_ingredient WHERE id_menu_item = ?", updateDto.id());

        for (MenuItemUpdateDto.MenuItemIngredientDto ingredientDto : updateDto.ingredients()) {
            Optional<Long> ingredientId = getIngredientIdByTitle(ingredientDto.ingredientTitle());

            if (ingredientId.isEmpty()) {
                throw new IllegalArgumentException("Ingredient not found: " + ingredientDto.ingredientTitle());
            }

            final String ingredientSql = """
                    INSERT INTO Menu_item_ingredient (id_ingredient, id_menu_item, quantity, measure)
                    VALUES (?, ?, ?, ?::measure)
                    """;

            jdbc.update(ingredientSql,
                    ingredientId.get(),
                    updateDto.id(),
                    ingredientDto.quantity(),
                    ingredientDto.measure().name());
        }

        log.info("Updated menu item with ID: {}", updateDto.id());
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        if (!menuItemExists(id)) {
            throw new IllegalArgumentException("Menu item not found with ID: " + id);
        }

        String imageUrl = getMenuItemImageUrl(id);
        if (imageUrl != null) {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            s3Service.deleteFile(fileName);
        }

        jdbc.update("DELETE FROM Menu_item_ingredient WHERE id_menu_item = ?", id);

        jdbc.update("DELETE FROM Menu_item WHERE id = ?", id);

        log.info("Deleted menu item with ID: {}", id);
    }

    private boolean menuItemExists(Long id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Menu_item WHERE id = ?",
                Integer.class,
                id);
        return count != null && count > 0;
    }

    private String getMenuItemImageUrl(Long id) {
        try {
            return jdbc.queryForObject(
                    "SELECT image_url FROM Menu_item WHERE id = ?",
                    String.class,
                    id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Optional<Long> getIngredientIdByTitle(String title) {
        try {
            Long id = jdbc.queryForObject(
                    "SELECT id FROM Ingredient WHERE title = ?",
                    Long.class,
                    title);
            return Optional.ofNullable(id);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public Optional<MenuItem> findByTitle(String title) {
        try {
            MenuItem menuItem = jdbc.queryForObject(
                    "SELECT * FROM Menu_item WHERE title = ?",
                    menuItemMapper,
                    title
            );
            if (menuItem != null) {
                loadMenuItemIngredients(menuItem);
                log.info("Found menu item with title: {}", title);
            }
            return Optional.ofNullable(menuItem);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Menu item not found with title: {}", title);
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public List<MenuItemUpdateDto.MenuItemIngredientDto> getMenuItemIngredients(Long menuItemId) {
        return jdbc.query(
            "SELECT i.title, mi.quantity, i.measure " +
            "FROM Menu_item_ingredient mi " +
            "JOIN Ingredient i ON mi.id_ingredient = i.id " +
            "WHERE mi.id_menu_item = ?",
            (rs, rowNum) -> new MenuItemUpdateDto.MenuItemIngredientDto(
                rs.getString("title"),
                rs.getBigDecimal("quantity"),
                Measure.valueOf(rs.getString("measure"))
            ),
            menuItemId
        );
    }

    public List<MenuItemResponseDto> getAllMenuItemsWithImages() {
        List<MenuItem> menuItems = getAllMenuItems();
        
        return menuItems.stream().map(menuItem -> {
            // Get ingredients for the menu item
            final String sql = """
                SELECT mi.*, i.title as ingredient_title 
                FROM Menu_item_ingredient mi
                JOIN Ingredient i ON mi.id_ingredient = i.id
                WHERE mi.id_menu_item = ?
                """;
            
            List<MenuItemResponseDto.IngredientDto> ingredients = jdbc.query(
                sql,
                (rs, rowNum) -> new MenuItemResponseDto.IngredientDto(
                    rs.getString("ingredient_title"),
                    rs.getBigDecimal("quantity"),
                    rs.getString("measure")
                ),
                menuItem.getId()
            );

            // Get and convert image to Base64
            byte[] imageBytes = s3Service.getFileByUrl(menuItem.getImageUrl());
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            return new MenuItemResponseDto(
                menuItem.getTitle(),
                menuItem.getPrice(),
                menuItem.getDescription(),
                ingredients,
                base64Image
            );
        }).collect(Collectors.toList());
    }
}
