package com.api.pulsetrack.modules.ingestion.producer;

import com.api.pulsetrack.modules.ingestion.dto.LocationIngestRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LocationProducerService {

    public static final String STREAM_KEY = "location:events";
    private final RedisTemplate<String, Object> redisTemplate;

    public LocationProducerService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void sendLocationEvent(LocationIngestRequest request) {
        Map<String, String> fields = new HashMap<>();
        fields.put("orderId", request.orderId().toString());
        fields.put("latitude", request.latitude().toString());
        fields.put("longitude", request.longitude().toString());
        fields.put("timestamp", String.valueOf(System.currentTimeMillis()));


        ObjectRecord<String, Map<String, String>> record = StreamRecords.newRecord()
                .ofObject(fields)
                .withStreamKey(STREAM_KEY);

        this.redisTemplate.opsForStream().add(record);
    }


}
