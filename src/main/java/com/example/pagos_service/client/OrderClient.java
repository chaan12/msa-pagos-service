package com.example.pagos_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.example.pagos_service.dto.OrderPaymentRequest;
import com.example.pagos_service.dto.OrderResponse;

@Component
public class OrderClient {

    private final RestTemplate restTemplate;
    private final String ordersUrl;

    public OrderClient(RestTemplate restTemplate,
            @Value("${services.orders-url:http://ordenes-service:8082/ordenes}") String ordersUrl) {
        this.restTemplate = restTemplate;
        this.ordersUrl = ordersUrl;
    }

    public OrderResponse getOrder(String orderId) {
        try {
            return restTemplate.getForObject(ordersUrl + "/" + orderId, OrderResponse.class);
        } catch (HttpStatusCodeException exception) {
            if (exception.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "No se pudo consultar la orden: " + exception.getStatusText(), exception);
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar la orden", exception);
        }
    }

    public OrderResponse applyPayment(String orderId, OrderPaymentRequest request) {
        try {
            restTemplate.put(ordersUrl + "/" + orderId + "/payment", request);
            return getOrder(orderId);
        } catch (HttpStatusCodeException exception) {
            if (exception.getStatusCode().is4xxClientError()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La orden rechazo el pago: " + exception.getResponseBodyAsString(), exception);
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "No se pudo actualizar el saldo de la orden", exception);
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "No se pudo actualizar el saldo de la orden", exception);
        }
    }
}
