package com.api.pulsetrack.modules.processing.consumer;


import com.api.pulsetrack.modules.order.model.Order;
import com.api.pulsetrack.modules.order.repo.OrderRepository;
import com.api.pulsetrack.modules.processing.dto.LocationCacheDTO;
import com.api.pulsetrack.modules.processing.model.OrderLocationHistory;
import com.api.pulsetrack.modules.processing.repo.OrderLocationHistoryRepository;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class LocationStreamConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final OrderRepository orderRepository;
    private final OrderLocationHistoryRepository historyRepository;
    private final ObjectMapper objectMapper;

    public LocationStreamConsumer(
            RedisTemplate<String, Object> redisTemplate,
            OrderRepository orderRepository,
            OrderLocationHistoryRepository historyRepository,
            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.orderRepository = orderRepository;
        this.historyRepository = historyRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> record) {

        try {
            Map<String, String> valueMap = record.getValue();
            Long orderId = Long.parseLong(valueMap.get("orderId"));
            BigDecimal latitude = new BigDecimal(valueMap.get("latitude"));
            BigDecimal longitude = new BigDecimal(valueMap.get("longitude"));
            Long timestamp = Long.parseLong(valueMap.get("timestamp"));

            String cacheKey = "order:location:" + orderId;
            LocationCacheDTO cacheData = new LocationCacheDTO(orderId, latitude, longitude, timestamp);
            String jsonValue = objectMapper.writeValueAsString(cacheData);
            redisTemplate.opsForValue().set(cacheKey, jsonValue);

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + orderId));

            OrderLocationHistory history = new OrderLocationHistory();
            history.setOrder(order);
            history.setLatitude(latitude);
            history.setLongitude(longitude);

            historyRepository.save(history);

            System.out.println("Worker [OK] -> Pedido: " + orderId + " | Lat: " + latitude + " | Lng: " + longitude);




        } catch (NumberFormatException e) {

            System.err.println("Erro ao processar mensagem do Redis Stream: " + e.getMessage());;
        }
    }



}
