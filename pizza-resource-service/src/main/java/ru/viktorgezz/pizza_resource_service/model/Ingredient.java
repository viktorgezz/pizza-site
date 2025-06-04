package ru.viktorgezz.pizza_resource_service.model;

import ru.viktorgezz.pizza_resource_service.util.Measure;

import java.math.BigDecimal;

public class Ingredient {

    private Long id;
    private String title;
    private BigDecimal quantity;
    private Measure measure;

    public Ingredient() {
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", title='" + title + '\'' +
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }
}
