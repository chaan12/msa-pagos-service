package com.example.pagos_service.chain;

public abstract class AbstractPaymentHandler implements PaymentHandler {

    private PaymentHandler next;

    @Override
    public PaymentHandler setNext(PaymentHandler next) {
        this.next = next;
        return next;
    }

    @Override
    public void handle(PaymentContext context) {
        doHandle(context);
        if (next != null) {
            next.handle(context);
        }
    }

    protected abstract void doHandle(PaymentContext context);
}
