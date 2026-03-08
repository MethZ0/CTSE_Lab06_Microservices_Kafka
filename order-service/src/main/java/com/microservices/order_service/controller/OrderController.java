package com.microservices.order_service.controller;

import com.microservices.order_service.model.Order;
import com.microservices.order_service.service.OrderProducerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderProducerService orderProducerService;

    public OrderController(OrderProducerService orderProducerService) {
        this.orderProducerService = orderProducerService;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Order order) {
        orderProducerService.publishOrder(order);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Order placed successfully! OrderId: " + order.getOrderId());
    }
}
