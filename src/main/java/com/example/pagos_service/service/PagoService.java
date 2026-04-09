package com.example.pagos_service.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.pagos_service.model.Pago;
import com.example.pagos_service.repository.PagoRepository;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public Pago procesarPago(Pago pago) {
        validatePago(pago);
        pago.setId(null);
        pago.setOrdenId(pago.getOrdenId().trim());
        pago.setEstado("procesado");
        return pagoRepository.save(pago);
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

    private void validatePago(Pago pago) {
        if (pago == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud es obligatorio");
        }
        if (pago.getOrdenId() == null || pago.getOrdenId().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ordenId es obligatorio");
        }
        if (pago.getMonto() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El monto del pago debe ser mayor que cero");
        }
    }

    private void validateId(String id, String message) {
        if (id == null || id.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
}
