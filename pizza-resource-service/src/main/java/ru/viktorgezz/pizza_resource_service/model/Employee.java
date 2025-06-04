package ru.viktorgezz.pizza_resource_service.model;

import jakarta.validation.constraints.*;

public class Employee extends User {

    @NotNull(message = "ID сотрудника не может быть пустым")
    private Long idEmployee;

    @NotBlank(message = "Описание должности обязательно")
    @Size(max = 255, message = "Описание должности не может быть длиннее 255 символов")
    private String jobDescription;

    @NotNull(message = "ID ресторана не может быть пустым")
    @Positive(message = "ID ресторана должен быть положительным числом")
    private Long idRestaurant;

    @Override
    public String toString() {
        return "Employee{" +
                "idEmployee=" + idEmployee +
                ", jobDescription='" + jobDescription + '\'' +
                ", idRestaurant=" + idRestaurant +
                '}';
    }

    public Long getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(Long idEmployee) {
        this.idEmployee = idEmployee;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public Long getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(Long idRestaurant) {
        this.idRestaurant = idRestaurant;
    }
}
