package com.example.pagos_service.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pagos_service.model.Pago;
import com.example.pagos_service.repository.PagoRepository;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    private static final Logger logger = LoggerFactory.getLogger(PagoController.class);
    private final PagoRepository repo;

    public PagoController(PagoRepository repo){
        this.repo = repo;
    }

    @PostMapping("/procesar")
    public ResponseEntity<Map<String, Object>> procesarPago(@RequestBody Pago pago){
        try {
            Pago savedPago = repo.save(pago);
            logger.info("Pago procesado correctamente. id={}, ordenId={}, monto={}",
                    savedPago.getId(), savedPago.getOrdenId(), savedPago.getMonto());
            return ResponseEntity.status(HttpStatus.CREATED).body(buildResponse(true, "Pago procesado correctamente", savedPago));
        } catch (Exception exception) {
            logger.error("Error al procesar pago. ordenId={}, monto={}", pago.getOrdenId(), pago.getMonto(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "No se pudo procesar el pago", null));
        }
    }

    @GetMapping("/{id}")
    public Pago obtenerPago(@PathVariable String id){
        return repo.findById(id).orElse(null);
    }

    @GetMapping
    public List<Pago> obtenerPagos(){
        return repo.findAll();
    }

    private Map<String, Object> buildResponse(boolean success, String message, Object data) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("data", data);
        return response;
    }
}
