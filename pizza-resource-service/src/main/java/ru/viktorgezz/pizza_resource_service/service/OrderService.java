package ru.viktorgezz.pizza_resource_service.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.viktorgezz.pizza_resource_service.dto.rs.OrderDto;
import ru.viktorgezz.pizza_resource_service.dto.rq.OrderRequestDto;
import ru.viktorgezz.pizza_resource_service.model.UserJwtInfo;
import ru.viktorgezz.pizza_resource_service.util.StatusOrder;
import ru.viktorgezz.pizza_resource_service.util.TypeOrder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class OrderService {
    
    private final JdbcTemplate jdbcTemplate;
    private final JwtService jwtService;
    private final CartService cartService;
    private final RowMapper<OrderDto> orderRowMapper;
    
    public OrderService(
            JdbcTemplate jdbcTemplate,
            JwtService jwtService,
            CartService cartService
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.jwtService = jwtService;
        this.cartService = cartService;
        
        this.orderRowMapper = (rs, rowNum) -> {
            Long orderId = rs.getLong("id");
            List<String> menuItems = this.jdbcTemplate.queryForList(
                """
                SELECT mi.title 
                FROM Order_item oi 
                JOIN Menu_item mi ON oi.id_menu_item = mi.id 
                WHERE oi.id_order = ?
                """,
                String.class,
                orderId
            );
            
            return new OrderDto(
                orderId,
                rs.getLong("id_customer"),
                rs.getLong("id_restaurant"),
                rs.getLong("id_courier"),
                StatusOrder.valueOf(rs.getString("status")),
                TypeOrder.valueOf(rs.getString("order_type")),
                rs.getTimestamp("date"),
                menuItems
            );
        };
    }

    // Получение всех заказов пользователя
    public List<OrderDto> getUserOrders() {
        UserJwtInfo userInfo = jwtService.getCurrentUserInfo();
        return jdbcTemplate.query(
            """
            SELECT o.* FROM _Order o
            JOIN Customer c ON o.id_customer = c.id
            WHERE c.user_id = ?
            ORDER BY o.date DESC
            """,
            orderRowMapper,
            userInfo.getId()
        );
    }
    
    // Получение всех заказов (для админов)
    public List<OrderDto> getAllOrders() {
        return jdbcTemplate.query(
            "SELECT * FROM _Order ORDER BY date DESC",
            orderRowMapper
        );
    }
    
    // Получение заказов по статусу и типу
    public List<OrderDto> getOrdersByStatusAndType(StatusOrder status, TypeOrder type) {
        return jdbcTemplate.query(
            """
            SELECT * FROM _Order 
            WHERE status = ?::status_order 
            AND order_type = ?::type_order 
            ORDER BY date DESC
            """,
            orderRowMapper,
            status.name(),
            type.name()
        );
    }
    
    @Transactional
    public void createOrder(OrderRequestDto orderRequest) {
        UserJwtInfo userInfo = jwtService.getCurrentUserInfo();
        
        // Получаем customer_id по user_id из таблицы Customer
        Long customerId = jdbcTemplate.queryForObject(
            "SELECT id FROM Customer WHERE user_id = ?",
            Long.class,
            userInfo.getId()
        );
        
        if (customerId == null) {
            throw new IllegalStateException("Customer not found for user ID: " + userInfo.getId());
        }
        
        Long orderId = jdbcTemplate.queryForObject(
            "INSERT INTO _Order (id_customer, id_restaurant, status, order_type, date) " +
            "VALUES (?, ?, 'PENDING', ?::type_order, ?) RETURNING id",
            Long.class,
            customerId, 1L, orderRequest.orderType(), Timestamp.from(Instant.now())
        );
        
        for (OrderRequestDto.CartItem item : orderRequest.items()) {
            Long menuItemId = jdbcTemplate.queryForObject(
                "SELECT id FROM Menu_item WHERE title = ?",
                Long.class,
                item.title()
            );
            
            jdbcTemplate.update(
                "INSERT INTO Order_item (id_order, id_menu_item, quantity, price) " +
                "VALUES (?, ?, ?, ?)",
                orderId, menuItemId, item.quantity(), BigDecimal.valueOf(item.price())
            );
            
            jdbcTemplate.update(
                """
                UPDATE Ingredient i
                SET quantity = i.quantity - (mi.quantity * ?)
                FROM Menu_item_ingredient mi
                WHERE mi.id_ingredient = i.id
                AND mi.id_menu_item = ?
                """,
                item.quantity(), menuItemId
            );

            cartService.clearCart();
        }
    }

    @Transactional
    public void updateOrder(Long orderId, StatusOrder newStatus, TypeOrder newType) {
        int updated = jdbcTemplate.update(
            """
            UPDATE _Order 
            SET status = ?::status_order,
                order_type = ?::type_order 
            WHERE id = ?
            """,
            newStatus.name(),
            newType.name(),
            orderId
        );
        
        if (updated == 0) {
            throw new IllegalStateException("Order not found with id: " + orderId);
        }
    }

    // Метод для проверки существования заказа
    public boolean orderExists(Long orderId) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM _Order WHERE id = ?",
            Integer.class,
            orderId
        );
        return count != null && count > 0;
    }

    // Метод для получения заказа по ID
    public OrderDto getOrderById(Long orderId) {
        List<OrderDto> orders = jdbcTemplate.query(
            "SELECT * FROM _Order WHERE id = ?",
            orderRowMapper,
            orderId
        );
        
        if (orders.isEmpty()) {
            throw new IllegalStateException("Order not found with id: " + orderId);
        }
        
        return orders.get(0);
    }

    // Получение заказов по статусу с детальной информацией
    public List<OrderDto> getOrdersByStatus(StatusOrder status) {
        return jdbcTemplate.query(
            """
            SELECT o.*, c.address as customer_address, c.phone as customer_phone,
                   u.username as customer_name, u.email as customer_email
            FROM _Order o
            JOIN Customer c ON o.id_customer = c.id
            JOIN _user u ON c.user_id = u.id
            WHERE o.status = ?::status_order
            ORDER BY o.date DESC
            """,
            orderRowMapper,
            status.name()
        );
    }
}
