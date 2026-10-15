package com.api.pulsetrack.modules.order.repo;

import com.api.pulsetrack.modules.order.model.Order;
import com.api.pulsetrack.modules.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByOrderStatus(OrderStatus orderStatus);
}
