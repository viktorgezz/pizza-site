package ru.viktorgezz.pizza_resource_service.model;


import java.math.BigDecimal;
import java.util.Objects;

public class MenuItem {

    private Long id;
    private Long idRestaurant;
    private String title;
    private BigDecimal price;
    private String description;
    private String imageUrl;

    public MenuItem() {
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", idRestaurant=" + idRestaurant +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return Objects.equals(id, menuItem.id) && Objects.equals(idRestaurant, menuItem.idRestaurant) && Objects.equals(title, menuItem.title) && Objects.equals(price, menuItem.price) && Objects.equals(description, menuItem.description) && Objects.equals(imageUrl, menuItem.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idRestaurant, title, price, description, imageUrl);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(Long idRestaurant) {
        this.idRestaurant = idRestaurant;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
