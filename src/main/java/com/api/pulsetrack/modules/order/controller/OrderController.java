package com.api.pulsetrack.modules.order.controller;


import com.api.pulsetrack.modules.order.dto.OrderCreateRequest;
import com.api.pulsetrack.modules.order.dto.OrderResponse;
import com.api.pulsetrack.modules.order.model.OrderStatus;
import com.api.pulsetrack.modules.order.service.OrderService;
import com.api.pulsetrack.modules.tracking.service.SseEmitterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    private final OrderService orderService;
    private final SseEmitterService sseEmitterService;

    public OrderController(OrderService orderService, SseEmitterService sseEmitterService) {
        this.orderService = orderService;
        this.sseEmitterService = sseEmitterService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid OrderCreateRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {
        OrderResponse response = orderService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/complete")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<OrderResponse> completeOrder(@PathVariable Long id) {
        OrderResponse completedOrder = orderService.completeOrder(id);
        sseEmitterService.closeStream(id);
        return ResponseEntity.ok(completedOrder);
    }

}
