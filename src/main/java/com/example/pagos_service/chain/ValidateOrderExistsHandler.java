package com.example.pagos_service.chain;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.example.pagos_service.client.OrderClient;
import com.example.pagos_service.dto.OrderResponse;
import com.example.pagos_service.model.Pago;

@Component
public class ValidateOrderExistsHandler extends AbstractPaymentHandler {

    private final OrderClient orderClient;

    public ValidateOrderExistsHandler(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @Override
    protected void doHandle(PaymentContext context) {
        Pago pago = context.getPago();
        if (pago == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud es obligatorio");
        }
        if (pago.getOrdenId() == null || pago.getOrdenId().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ordenId es obligatorio");
        }
        pago.setOrdenId(pago.getOrdenId().trim());
        OrderResponse order = orderClient.getOrder(pago.getOrdenId());
        if (order == null || order.getId() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada");
        }
        context.setOrder(order);
    }
}
