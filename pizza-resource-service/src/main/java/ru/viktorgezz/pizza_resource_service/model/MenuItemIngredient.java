package ru.viktorgezz.pizza_resource_service.model;

import ru.viktorgezz.pizza_resource_service.util.Measure;

import java.math.BigDecimal;

public class MenuItemIngredient {

    private Long id;
    private Long idIngredient;
    private Long idMenuItem;
    private BigDecimal quantity;
    private Measure measure;

    public MenuItemIngredient() {
    }

    @Override
    public String toString() {
        return "MenuItemIngredient{" +
                "id=" + id +
                ", idIngredient=" + idIngredient +
                ", idMenuItem=" + idMenuItem +
                ", quantity=" + quantity +
                ", measure=" + measure +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdIngredient() {
        return idIngredient;
    }

    public void setIdIngredient(Long idIngredient) {
        this.idIngredient = idIngredient;
    }

    public Long getIdMenuItem() {
        return idMenuItem;
    }

    public void setIdMenuItem(Long idMenuItem) {
        this.idMenuItem = idMenuItem;
    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
