package com.example.pagos_service.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.pagos_service.dto.PaymentReceivedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PaymentEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public PaymentEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
            @Value("${kafka.topics.payment-received:payment_received_events}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    public void publish(PaymentReceivedEvent event) {
        try {
            kafkaTemplate.send(topic, event.getOrderId(), objectMapper.writeValueAsString(event));
            logger.info("Payment received event published. topic={}, eventId={}, orderId={}, paymentId={}",
                    topic, event.getEventId(), event.getOrderId(), event.getPaymentId());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("No se pudo serializar el evento de pago recibido", exception);
        }
    }
}
