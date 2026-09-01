package com.api.pulsetrack.modules.tracking.dto;

import java.math.BigDecimal;

public record CurrentLocationResponse(Long orderId,
                                      BigDecimal latitude,
                                      BigDecimal longitude,
                                      Long timestamp) {
}
