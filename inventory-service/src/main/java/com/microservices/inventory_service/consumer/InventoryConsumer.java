package com.microservices.inventory_service.consumer;

import com.microservices.inventory_service.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryConsumer.class);

    @KafkaListener(topics = "order-topic", groupId = "inventory-group")
    public void consumeOrder(Order order) {
        log.info("Inventory Service received order → orderId={}, item={}, quantity={}",
                order.getOrderId(), order.getItem(), order.getQuantity());
        log.info("Stock Updated");
    }
}
