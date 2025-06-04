package ru.viktorgezz.pizza_resource_service.model;

import java.math.BigDecimal;

public class OrderItem {

    private Long id;
    private Long idOrder;
    private Long idMenuItem;
    private BigDecimal quantity;
    private BigDecimal price;

    public OrderItem() {
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", idOrder=" + idOrder +
                ", idMenuItem=" + idMenuItem +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(Long idOrder) {
        this.idOrder = idOrder;
    }

    public Long getIdMenuItem() {
        return idMenuItem;
    }

    public void setIdMenuItem(Long idMenuItem) {
        this.idMenuItem = idMenuItem;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
