package com.example.pagos_service.chain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pagos_service.client.OrderClient;
import com.example.pagos_service.dto.OrderPaymentRequest;
import com.example.pagos_service.dto.OrderResponse;
import com.example.pagos_service.dto.PaymentReceivedEvent;
import com.example.pagos_service.messaging.PaymentEventPublisher;
import com.example.pagos_service.model.Pago;
import com.example.pagos_service.repository.PagoRepository;

@Component
public class ApplyPaymentHandler extends AbstractPaymentHandler {

    private final PagoRepository pagoRepository;
    private final OrderClient orderClient;
    private final PaymentEventPublisher paymentEventPublisher;

    public ApplyPaymentHandler(PagoRepository pagoRepository, OrderClient orderClient,
            PaymentEventPublisher paymentEventPublisher) {
        this.pagoRepository = pagoRepository;
        this.orderClient = orderClient;
        this.paymentEventPublisher = paymentEventPublisher;
    }

    @Override
    protected void doHandle(PaymentContext context) {
        Pago pago = findOrCreatePayment(context.getPago());
        OrderResponse updatedOrder = orderClient.applyPayment(pago.getOrdenId(),
                new OrderPaymentRequest(pago.getMonto(), pago.getId()));
        pago.setEstado("PROCESADO");
        Pago savedPago = pagoRepository.save(pago);
        context.setSavedPago(savedPago);
        context.setUpdatedOrder(updatedOrder);
        paymentEventPublisher.publish(buildEvent(savedPago, updatedOrder));
    }

    private Pago findOrCreatePayment(Pago pago) {
        if (pago.getId() != null && !pago.getId().trim().isEmpty()) {
            return pagoRepository.findById(pago.getId().trim())
                    .orElseGet(() -> prepareNewPayment(pago, pago.getId().trim()));
        }
        return prepareNewPayment(pago, UUID.randomUUID().toString());
    }

    private Pago prepareNewPayment(Pago pago, String paymentId) {
        pago.setId(paymentId);
        return pago;
    }

    private PaymentReceivedEvent buildEvent(Pago pago, OrderResponse order) {
        PaymentReceivedEvent event = new PaymentReceivedEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setPaymentId(pago.getId());
        event.setOrderId(pago.getOrdenId());
        event.setUsuarioId(order.getUsuarioId());
        event.setMonto(pago.getMonto());
        event.setSaldoRestante(order.getSaldoRestante());
        event.setOrderStatus(order.getStatus());
        event.setFullyPaid(order.getSaldoRestante() != null
                && order.getSaldoRestante().compareTo(BigDecimal.ZERO) == 0);
        event.setNotificationEmail(order.getNotificationEmail());
        event.setCreatedAt(Instant.now());
        return event;
    }
}
