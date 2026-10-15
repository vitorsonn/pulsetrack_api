package com.api.pulsetrack.modules.tracking.service;

import com.api.pulsetrack.modules.tracking.dto.LocationTrackingResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class TrackingUpdatePublisher {

    public static final String CHANNEL = "tracking:location-updates";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public TrackingUpdatePublisher(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(LocationTrackingResponse payload) {
        try {
            String message = objectMapper.writeValueAsString(payload);
            redisTemplate.convertAndSend(CHANNEL, message);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao publicar atualização de tracking", e);
        }
    }
}
