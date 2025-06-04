package ru.viktorgezz.pizza_resource_service.dto.rs;

import ru.viktorgezz.pizza_resource_service.util.StatusRestaurant;

import java.sql.Time;

public class RestaurantDto {
    private Long id;
    private String address;
    private StatusRestaurant status;
    private Time openingTime;
    private Time closingTime;

    public RestaurantDto() {
    }

    public RestaurantDto(Long id, String address, StatusRestaurant status, Time openingTime, Time closingTime) {
        this.id = id;
        this.address = address;
        this.status = status;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public StatusRestaurant getStatus() {
        return status;
    }

    public void setStatus(StatusRestaurant status) {
        this.status = status;
    }

    public Time getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Time openingTime) {
        this.openingTime = openingTime;
    }

    public Time getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(Time closingTime) {
        this.closingTime = closingTime;
    }
}
