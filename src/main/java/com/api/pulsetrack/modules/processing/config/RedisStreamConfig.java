package com.api.pulsetrack.modules.processing.config;


import com.api.pulsetrack.modules.ingestion.producer.LocationProducerService;
import com.api.pulsetrack.modules.processing.consumer.LocationStreamConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;

@Configuration
public class RedisStreamConfig {

    public static final String CONSUMER_GROUP = "tracking-processors";

    @Value("${INSTANCE_ID:}")
    private String configuredInstanceId;

    @Bean
    public Subscription subscription(
            RedisConnectionFactory connectionFactory,
            RedisTemplate<String, Object> redisTemplate,
            LocationStreamConsumer streamConsumer,
            String streamConsumerName) {

        createConsumerGroup(redisTemplate);

        var options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(1))
                .build();

        var container = StreamMessageListenerContainer.create(connectionFactory, options);

        var subscription = container.receive(
                Consumer.from(CONSUMER_GROUP, streamConsumerName),
                StreamOffset.create(
                        LocationProducerService.STREAM_KEY,
                        ReadOffset.lastConsumed()
                ),
                streamConsumer
        );

        container.start();
        return subscription;
    }

    private void createConsumerGroup(RedisTemplate<String, Object> redisTemplate) {
        try {
            redisTemplate.opsForStream().createGroup(
                    LocationProducerService.STREAM_KEY,
                    ReadOffset.from("0-0"),
                    CONSUMER_GROUP
            );
        } catch (RuntimeException exception) {
            String message = exception.getMessage();
            if (message == null || !message.contains("BUSYGROUP")) {
                throw exception;
            }
        }
    }

    @Bean
    public String streamConsumerName() {
        return consumerName();
    }

    private String consumerName() {
        if (configuredInstanceId != null && !configuredInstanceId.isBlank()) {
            return configuredInstanceId;
        }

        String hostname = System.getenv("HOSTNAME");
        return hostname != null && !hostname.isBlank() ? hostname : "api-" + System.currentTimeMillis();
    }
}
