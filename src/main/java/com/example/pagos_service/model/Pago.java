package com.example.pagos_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="pagos")
public class Pago {

    @Id
    private String id;
    private String ordenId;
    private double monto;
    private String estado;

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getOrdenId(){
        return ordenId;
    }

    public void setOrdenId(String ordenId){
        this.ordenId = ordenId;
    }

    public double getMonto(){
        return monto;
    }

    public void setMonto(double monto){
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
