package com.example.pagos_service.dto;

import java.math.BigDecimal;

public class OrderPaymentRequest {

    private BigDecimal monto;
    private String paymentId;

    public OrderPaymentRequest() {
    }

    public OrderPaymentRequest(BigDecimal monto, String paymentId) {
        this.monto = monto;
        this.paymentId = paymentId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
