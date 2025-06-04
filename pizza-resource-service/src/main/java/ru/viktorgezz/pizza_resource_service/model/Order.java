package ru.viktorgezz.pizza_resource_service.model;

import ru.viktorgezz.pizza_resource_service.util.StatusOrder;
import ru.viktorgezz.pizza_resource_service.util.TypeOrder;

import java.sql.Timestamp;

public class Order {

    private Long id;
    private Long idCustomer;
    private Long idRestaurant;
    private Long idCourier;
    private StatusOrder status;
    private TypeOrder typeOrder;
    private Timestamp time;

    public Order() {
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", idCustomer=" + idCustomer +
                ", idRestaurant=" + idRestaurant +
                ", idCourier=" + idCourier +
                ", status=" + status +
                ", typeOrder=" + typeOrder +
                ", time=" + time +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(Long idCustomer) {
        this.idCustomer = idCustomer;
    }

    public Long getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(Long idRestaurant) {
        this.idRestaurant = idRestaurant;
    }

    public Long getIdCourier() {
        return idCourier;
    }

    public void setIdCourier(Long idCourier) {
        this.idCourier = idCourier;
    }

    public StatusOrder getStatus() {
        return status;
    }

    public void setStatus(StatusOrder status) {
        this.status = status;
    }

    public TypeOrder getTypeOrder() {
        return typeOrder;
    }

    public void setTypeOrder(TypeOrder typeOrder) {
        this.typeOrder = typeOrder;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
