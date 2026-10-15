package com.api.pulsetrack.modules.tracking.service;

import com.api.pulsetrack.modules.tracking.dto.LocationTrackingResponse;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Component
public class TrackingUpdateSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SseEmitterService sseEmitterService;

    public TrackingUpdateSubscriber(
            ObjectMapper objectMapper,
            SseEmitterService sseEmitterService) {
        this.objectMapper = objectMapper;
        this.sseEmitterService = sseEmitterService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String payloadJson = new String(message.getBody(), StandardCharsets.UTF_8);
            LocationTrackingResponse payload =
                    objectMapper.readValue(payloadJson, LocationTrackingResponse.class);
            sseEmitterService.sendLocationUpdate(payload);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao processar atualização de tracking do Pub/Sub", e);
        }
    }
}
