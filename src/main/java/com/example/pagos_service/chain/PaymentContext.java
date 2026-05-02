package com.example.pagos_service.chain;

import com.example.pagos_service.dto.OrderResponse;
import com.example.pagos_service.model.Pago;

public class PaymentContext {

    private final Pago pago;
    private OrderResponse order;
    private OrderResponse updatedOrder;
    private Pago savedPago;

    public PaymentContext(Pago pago) {
        this.pago = pago;
    }

    public Pago getPago() {
        return pago;
    }

    public OrderResponse getOrder() {
        return order;
    }

    public void setOrder(OrderResponse order) {
        this.order = order;
    }

    public OrderResponse getUpdatedOrder() {
        return updatedOrder;
    }

    public void setUpdatedOrder(OrderResponse updatedOrder) {
        this.updatedOrder = updatedOrder;
    }

    public Pago getSavedPago() {
        return savedPago;
    }

    public void setSavedPago(Pago savedPago) {
        this.savedPago = savedPago;
    }
}
