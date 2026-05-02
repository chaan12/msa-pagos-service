package com.example.pagos_service.chain;

public interface PaymentHandler {

    PaymentHandler setNext(PaymentHandler next);

    void handle(PaymentContext context);
}
