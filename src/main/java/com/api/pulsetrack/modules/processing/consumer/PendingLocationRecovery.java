package com.api.pulsetrack.modules.processing.consumer;

import com.api.pulsetrack.modules.ingestion.producer.LocationProducerService;
import com.api.pulsetrack.modules.processing.config.RedisStreamConfig;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class PendingLocationRecovery {

    private static final int MAX_MESSAGES_PER_RUN = 100;
    private static final Duration MIN_IDLE_TIME = Duration.ofSeconds(30);

    private final RedisTemplate<String, Object> redisTemplate;
    private final LocationStreamConsumer streamConsumer;
    private final String consumerName;

    public PendingLocationRecovery(
            RedisTemplate<String, Object> redisTemplate,
            LocationStreamConsumer streamConsumer,
            String streamConsumerName) {
        this.redisTemplate = redisTemplate;
        this.streamConsumer = streamConsumer;
        this.consumerName = streamConsumerName;
    }

    @Scheduled(fixedDelayString = "${tracking.stream.recovery-delay-ms:10000}")
    public void recoverPendingMessages() {
        PendingMessages pendingMessages = redisTemplate.opsForStream().pending(
                LocationProducerService.STREAM_KEY,
                RedisStreamConfig.CONSUMER_GROUP,
                org.springframework.data.domain.Range.unbounded(),
                MAX_MESSAGES_PER_RUN
        );

        for (PendingMessage pendingMessage : pendingMessages) {
            if (pendingMessage.getElapsedTimeSinceLastDelivery().compareTo(MIN_IDLE_TIME) < 0) {
                continue;
            }

            var claimedRecords = redisTemplate.opsForStream().claim(
                    LocationProducerService.STREAM_KEY,
                    RedisStreamConfig.CONSUMER_GROUP,
                    consumerName,
                    MIN_IDLE_TIME,
                    pendingMessage.getId()
            );

            for (MapRecord<String, Object, Object> claimedRecord : claimedRecords) {
                @SuppressWarnings("unchecked")
                MapRecord<String, String, String> typedRecord =
                        (MapRecord<String, String, String>) (MapRecord<?, ?, ?>) claimedRecord;
                streamConsumer.processAndAcknowledge(typedRecord);
            }
        }
    }
}
