package com.api.pulsetrack.modules.ingestion.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LocationIngestRequest(@NotNull(message = "O ID do pedido é obrigatório")
                                    Long orderId,

                                    @NotNull(message = "A latitude é obrigatória")
                                    BigDecimal latitude,

                                    @NotNull(message = "A longitude é obrigatória")
                                    BigDecimal longitude) {
}
