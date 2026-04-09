package com.example.pagos_service.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.example.pagos_service.messaging.PaymentRetryPublisher;
import com.example.pagos_service.service.PagoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PagoController.class)
@Import(PaymentRetryPublisher.class)
@TestPropertySource(properties = "broker.topics.payments=payments_retry_jobs")
class PagoControllerRetryPublishingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PagoService pagoService;

    @MockitoBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void shouldPublishPaymentRetryMessageWhenUnexpectedErrorOccurs() throws Exception {
        when(pagoService.procesarPago(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new IllegalStateException("Mongo no disponible"));

        mockMvc.perform(post("/pagos/procesar")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "ordenId": "ord-123",
                                  "monto": 199.99,
                                  "estado": "pendiente"
                                }
                                """))
                .andExpect(status().isInternalServerError());

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("payments_retry_jobs"), payloadCaptor.capture());

        JsonNode root = objectMapper.readTree(payloadCaptor.getValue());
        JsonNode data = root.get("data");

        org.junit.jupiter.api.Assertions.assertEquals("ord-123", data.get("ordenId").asText());
        org.junit.jupiter.api.Assertions.assertEquals(199.99d, data.get("monto").asDouble());
        org.junit.jupiter.api.Assertions.assertEquals("pendiente", data.get("estado").asText());
        org.junit.jupiter.api.Assertions.assertEquals("PENDING", root.get("sendEmail").get("status").asText());
        org.junit.jupiter.api.Assertions.assertEquals("PENDING", root.get("updateRetryJobs").get("status").asText());
    }

    @Test
    void shouldNotPublishRetryMessageWhenClientErrorOccurs() throws Exception {
        when(pagoService.procesarPago(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "El ordenId es obligatorio"));

        mockMvc.perform(post("/pagos/procesar")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "ordenId": "",
                                  "monto": 199.99
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(kafkaTemplate, never()).send(eq("payments_retry_jobs"), org.mockito.ArgumentMatchers.anyString());
    }
}
