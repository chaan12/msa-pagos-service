package com.example.pagos_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.pagos_service.model.Pago;

public interface PagoRepository extends MongoRepository<Pago,String>{
    List<Pago> findByOrdenId(String ordenId);
}
