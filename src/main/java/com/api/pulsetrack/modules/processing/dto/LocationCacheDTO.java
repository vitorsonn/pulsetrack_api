package com.api.pulsetrack.modules.processing.dto;

import java.math.BigDecimal;

public record LocationCacheDTO(Long orderId,
                               BigDecimal latitude,
                               BigDecimal longitude,
                               Long timestamp) {
}
