package com.api.pulsetrack.modules.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrderCreateRequest(
        @NotBlank(message = "O nome do cliente é obrigatório")
        @Size(max = 100, message = "O nome do cliente deve ter no máximo 100 caracteres")
        String customerName,

        @NotBlank(message = "O endereço de entrega é obrigatório")
        String deliveryAddress
) {
}
