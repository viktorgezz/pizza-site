package ru.viktorgezz.pizza_resource_service.model;

import jakarta.validation.constraints.*;
import ru.viktorgezz.pizza_resource_service.util.StatusRestaurant;

import java.sql.Time;
import java.util.Objects;

public class Restaurant {

    @NotNull(message = "ID ресторана не может быть пустым")
    private Long id;

    @NotBlank(message = "Адрес обязателен")
    @Size(max = 255, message = "Адрес не может быть длиннее 255 символов")
    private String address;

    @NotNull(message = "Статус ресторана обязателен")
    private StatusRestaurant status;

    private Time open;
    private Time close;

    public Restaurant() {
    }

    public Restaurant(
            Long id,
            String address,
            StatusRestaurant status,
            Time open,
            Time close) {
        this.id = id;
        this.address = address;
        this.status = status;
        this.open = open;
        this.close = close;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(address, that.address) &&
                status == that.status &&
                Objects.equals(open, that.open) &&
                Objects.equals(close, that.close);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, status, open, close);
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", status=" + status +
                ", open=" + open +
                ", close=" + close +
                '}';
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

    public Time getOpen() {
        return open;
    }

    public void setOpen(Time open) {
        this.open = open;
    }

    public Time getClose() {
        return close;
    }

    public void setClose(Time close) {
        this.close = close;
    }
}
