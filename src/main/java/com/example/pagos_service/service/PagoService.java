package com.example.pagos_service.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.pagos_service.chain.ApplyPaymentHandler;
import com.example.pagos_service.chain.PaymentContext;
import com.example.pagos_service.chain.PaymentHandler;
import com.example.pagos_service.chain.ValidateOrderExistsHandler;
import com.example.pagos_service.chain.ValidatePaymentAmountHandler;
import com.example.pagos_service.chain.ValidateSaldoRestanteHandler;
import com.example.pagos_service.model.Pago;
import com.example.pagos_service.repository.PagoRepository;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PaymentHandler paymentValidationChain;

    public PagoService(PagoRepository pagoRepository,
            ValidateOrderExistsHandler validateOrderExistsHandler,
            ValidateSaldoRestanteHandler validateSaldoRestanteHandler,
            ValidatePaymentAmountHandler validatePaymentAmountHandler,
            ApplyPaymentHandler applyPaymentHandler) {
        this.pagoRepository = pagoRepository;
        validateOrderExistsHandler
                .setNext(validateSaldoRestanteHandler)
                .setNext(validatePaymentAmountHandler)
                .setNext(applyPaymentHandler);
        this.paymentValidationChain = validateOrderExistsHandler;
    }

    public Pago procesarPago(Pago pago) {
        PaymentContext context = new PaymentContext(pago);
        paymentValidationChain.handle(context);
        return context.getSavedPago();
    }

    public Pago obtenerPagoPorId(String id) {
        validateId(id, "El id del pago es obligatorio");
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado"));
    }

    public List<Pago> obtenerPagos() {
        return pagoRepository.findAll();
    }

    public List<Pago> obtenerPagosPorOrden(String ordenId) {
        validateId(ordenId, "El id de la orden es obligatorio");
        return pagoRepository.findByOrdenId(ordenId.trim());
    }

    public Pago reembolsarPago(String id) {
        Pago pago = obtenerPagoPorId(id);
        if ("reembolsado".equalsIgnoreCase(pago.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pago ya fue reembolsado");
        }
        pago.setEstado("reembolsado");
        return pagoRepository.save(pago);
    }

    private void validateId(String id, String message) {
        if (id == null || id.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
}
