package com.microservices.billing_service.consumer;

import com.microservices.billing_service.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BillingConsumer {

    private static final Logger log = LoggerFactory.getLogger(BillingConsumer.class);

    @KafkaListener(topics = "order-topic", groupId = "billing-group")
    public void consumeOrder(Order order) {
        log.info("Billing Service received order → orderId={}, item={}, quantity={}",
                order.getOrderId(), order.getItem(), order.getQuantity());
        log.info("Invoice Generated");
    }
}
