package com.api.pulsetrack.modules.order.service;

import com.api.pulsetrack.modules.order.dto.OrderCreateRequest;
import com.api.pulsetrack.modules.order.dto.OrderResponse;
import com.api.pulsetrack.modules.order.model.Order;
import com.api.pulsetrack.modules.order.model.OrderStatus;
import com.api.pulsetrack.modules.order.repo.OrderRepository;
import com.api.pulsetrack.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        Order order = new Order();
        order.setCustomerName(request.customerName());
        order.setDeliveryAddress(request.deliveryAddress());
        order.setOrderStatus(OrderStatus.CREATED);
        Order savedOrder = orderRepository.save(order);
        return OrderResponse.fromEntity(savedOrder);
    }




    public List<OrderResponse> findAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getCustomerName(),
                        order.getDeliveryAddress(),
                        order.getOrderStatus(),
                        order.getCreatedAt(),
                        order.getUpdatedAt()
                ))
                .toList();
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido não encontrado com o ID: " + orderId));
        return OrderResponse.fromEntity(order);
    }

    public List<OrderResponse> findOrdersByStatus(OrderStatus status) {
        return orderRepository.findByOrderStatus(status)
                .stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getCustomerName(),
                        order.getDeliveryAddress(),
                        order.getOrderStatus(),
                        order.getCreatedAt(),
                        order.getUpdatedAt()
                ))
                .toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido não encontrado para o ID: " + id));

        order.setOrderStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return OrderResponse.fromEntity(updatedOrder);
    }

    @Transactional
    public OrderResponse completeOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido não encontrado para o ID: " + id));

        order.setOrderStatus(OrderStatus.DELIVERED);
        Order completedOrder = orderRepository.save(order);
        return OrderResponse.fromEntity(completedOrder);
    }


}
