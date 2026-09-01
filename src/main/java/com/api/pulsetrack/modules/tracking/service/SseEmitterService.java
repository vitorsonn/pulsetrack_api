package com.api.pulsetrack.modules.tracking.service;


import com.api.pulsetrack.modules.tracking.dto.LocationTrackingResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long orderId) {
        SseEmitter emitter = new SseEmitter(1800000L);
        this.emitters.put(orderId, emitter);

        emitter.onCompletion(() -> this.emitters.remove(orderId));
        emitter.onTimeout(() -> this.emitters.remove(orderId));
        emitter.onError((e) -> this.emitters.remove(orderId));
        return emitter;
    }

    public void sendLocationUpdate(LocationTrackingResponse payload) {
        SseEmitter emitter = this.emitters.get(payload.orderId());
        if (emitter != null) {

            try {
                emitter.send(SseEmitter.event()
                        .name("location-update")
                        .data(payload));

            } catch (Exception e) {
                this.emitters.remove(payload.orderId());
            }
        }
    }

}
