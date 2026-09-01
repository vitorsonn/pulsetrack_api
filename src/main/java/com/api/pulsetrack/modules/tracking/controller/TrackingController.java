package com.api.pulsetrack.modules.tracking.controller;

import com.api.pulsetrack.modules.tracking.dto.CurrentLocationResponse;
import com.api.pulsetrack.modules.tracking.service.SseEmitterService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tools.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/tracking")
public class TrackingController {

    private final SseEmitterService sseEmitterService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public TrackingController(SseEmitterService sseEmitterService, RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.sseEmitterService = sseEmitterService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping(path = "/stream/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeOrderTracking(@PathVariable Long orderId) {
        return sseEmitterService.subscribe(orderId);
    }

    @GetMapping("/{orderId}/current")
    public ResponseEntity<CurrentLocationResponse> getCurrentLocation(@PathVariable Long orderId) {
        String cacheKey = "order:location:" + orderId;
        Object rawJson = redisTemplate.opsForValue().get(cacheKey);

        if (rawJson == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            CurrentLocationResponse response = objectMapper.readValue(rawJson.toString(), CurrentLocationResponse.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


}
