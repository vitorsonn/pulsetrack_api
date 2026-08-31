package com.api.pulsetrack.modules.order.service;

import com.api.pulsetrack.modules.order.dto.OrderCreateRequest;
import com.api.pulsetrack.modules.order.dto.OrderResponse;
import com.api.pulsetrack.modules.order.model.Order;
import com.api.pulsetrack.modules.order.model.OrderStatus;
import com.api.pulsetrack.modules.order.repo.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request){
        Order order = new Order();
        order.setCustomerName(request.customerName());
        order.setDeliveryAddress(request.deliveryAddress());
        order.setOrderStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);

        Order savedOrder = orderRepository.save(order);
        return OrderResponse.fromEntity(savedOrder);

    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com o ID: " + orderId));
        return OrderResponse.fromEntity(order);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado para o ID: " + id));

        order.setOrderStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return OrderResponse.fromEntity(updatedOrder);
    }


}
