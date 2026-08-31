package com.api.pulsetrack.modules.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.OffsetDateTime;



    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Table(name = "orders")
    @Entity
    public class Order {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "customer_name", nullable = false, length = 100)
        private String customerName;

        @Column(name = "delivery_address", nullable = false, length = 255)
        private String deliveryAddress;

        @Enumerated(EnumType.STRING)
        private OrderStatus orderStatus;

        @Column(name = "created_at", nullable = false, updatable = false)
        private OffsetDateTime createdAt;

        @Column(name = "updated_at", nullable = false)
        private OffsetDateTime updatedAt;

        //metodos que definem o comportamento do entity listener para definir os valores de createdAt e updatedAt antes de persistir ou atualizar a entidade
        @PrePersist
        protected void onCreate() {
            this.createdAt = OffsetDateTime.now();
            this.updatedAt = OffsetDateTime.now();
        }

        @PreUpdate
        protected void onUpdate() {
            this.updatedAt = OffsetDateTime.now();
        }


    }
