package com.example.pagos_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.pagos_service.model.Pago;

public interface PagoRepository extends MongoRepository<Pago,String>{
}