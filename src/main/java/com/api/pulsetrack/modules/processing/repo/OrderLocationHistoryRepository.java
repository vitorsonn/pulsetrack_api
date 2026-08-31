package com.api.pulsetrack.modules.processing.repo;

import com.api.pulsetrack.modules.processing.model.OrderLocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLocationHistoryRepository extends JpaRepository<OrderLocationHistory, Long> {
}
