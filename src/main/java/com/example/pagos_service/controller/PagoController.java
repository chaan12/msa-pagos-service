package com.example.pagos_service.controller;

import java.util.List;

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

    private final PagoRepository repo;

    public PagoController(PagoRepository repo){
        this.repo = repo;
    }

    @PostMapping("/procesar")
    public Pago procesarPago(@RequestBody Pago pago){
        return repo.save(pago);
    }

    @GetMapping("/{id}")
    public Pago obtenerPago(@PathVariable String id){
        return repo.findById(id).orElse(null);
    }

    @GetMapping
    public List<Pago> obtenerPagos(){
        return repo.findAll();
    }

}