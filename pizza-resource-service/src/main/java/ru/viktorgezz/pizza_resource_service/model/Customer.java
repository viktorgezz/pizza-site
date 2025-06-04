package ru.viktorgezz.pizza_resource_service.model;

import jakarta.validation.constraints.*;
import java.util.Objects;

public class Customer extends User {

    @NotNull(message = "ID покупателя не может быть пустым")
    private Long idCustomer;

    @NotBlank(message = "Адрес обязателен")
    @Size(max = 255, message = "Адрес не может быть длиннее 255 символов")
    private String address;

    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Номер телефона должен быть в формате +7XXXXXXXXXX")
    @Size(max = 50, message = "Номер телефона не может быть длиннее 50 символов")
    private String phone;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(idCustomer, customer.idCustomer) && Objects.equals(address, customer.address) && Objects.equals(phone, customer.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idCustomer, address, phone);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "idCustomer=" + idCustomer +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public Long getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(Long idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
