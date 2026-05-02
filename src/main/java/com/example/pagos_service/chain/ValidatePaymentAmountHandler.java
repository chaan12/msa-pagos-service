package com.example.pagos_service.chain;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.example.pagos_service.model.Pago;

@Component
public class ValidatePaymentAmountHandler extends AbstractPaymentHandler {

    @Override
    protected void doHandle(PaymentContext context) {
        Pago pago = context.getPago();
        if (pago.getMonto() == null || pago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El monto del pago debe ser mayor que cero");
        }
        if (pago.getMonto().compareTo(context.getOrder().getSaldoRestante()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El monto del pago no puede ser mayor que el saldo_restante");
        }
    }
}
