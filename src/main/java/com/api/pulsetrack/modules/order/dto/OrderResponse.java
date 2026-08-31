package com.api.pulsetrack.modules.order.dto;

import com.api.pulsetrack.modules.order.model.Order;
import com.api.pulsetrack.modules.order.model.OrderStatus;

import java.time.OffsetDateTime;

public record OrderResponse(Long id,
                            String customerName,
                            String deliveryAddress,
                            OrderStatus status,
                            OffsetDateTime createdAt,
                            OffsetDateTime updatedAt)

{

    public static OrderResponse fromEntity(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getDeliveryAddress(),
                order.getOrderStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
