package com.example.pagos_service.chain;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.example.pagos_service.dto.OrderResponse;

@Component
public class ValidateSaldoRestanteHandler extends AbstractPaymentHandler {

    @Override
    protected void doHandle(PaymentContext context) {
        OrderResponse order = context.getOrder();
        if (order.getSaldoRestante() == null || order.getSaldoRestante().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La orden tiene un saldo_restante invalido");
        }
        if (order.getSaldoRestante().compareTo(BigDecimal.ZERO) == 0 || "PAGADO".equalsIgnoreCase(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La orden ya esta pagada");
        }
    }
}
