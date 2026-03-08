package com.microservices.order_service.service;

import com.microservices.order_service.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducerService {

    private static final Logger log = LoggerFactory.getLogger(OrderProducerService.class);
    private static final String TOPIC = "order-topic";

    private final KafkaTemplate<String, Order> kafkaTemplate;

    public OrderProducerService(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrder(Order order) {
        kafkaTemplate.send(TOPIC, order.getOrderId(), order);
        log.info("Order Service: Event Published → orderId={}, item={}, quantity={}",
                order.getOrderId(), order.getItem(), order.getQuantity());
    }
}
