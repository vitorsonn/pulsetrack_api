package com.api.pulsetrack.modules.order.repo;

import com.api.pulsetrack.modules.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
