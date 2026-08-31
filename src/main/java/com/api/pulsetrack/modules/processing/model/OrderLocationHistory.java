package com.api.pulsetrack.modules.processing.model;


import com.api.pulsetrack.modules.order.model.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
    @Table(name = "order_location_history")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class OrderLocationHistory {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "order_id", nullable = false)
        private Order order;

        @Column(nullable = false, precision = 10, scale = 7)
        private BigDecimal latitude;

        @Column(nullable = false, precision = 10, scale = 7)
        private BigDecimal longitude;

        @Column(name = "recorded_at", nullable = false, updatable = false)
        private OffsetDateTime recordedAt;

        @PrePersist
        protected void onCreate() {
            this.recordedAt = OffsetDateTime.now();
        }

    }


