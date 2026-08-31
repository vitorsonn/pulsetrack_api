package com.api.pulsetrack.modules.processing.config;


import com.api.pulsetrack.modules.ingestion.producer.LocationProducerService;
import com.api.pulsetrack.modules.processing.consumer.LocationStreamConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;

@Configuration
public class RedisStreamConfig {

    @Bean
    public Subscription subscription(
            RedisConnectionFactory connectionFactory,
            LocationStreamConsumer streamConsumer) {

        var options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(1))
                .build();

        var container = StreamMessageListenerContainer.create(connectionFactory, options);

        var subscription = container.receive(
                StreamOffset.create(
                        LocationProducerService.STREAM_KEY,
                        ReadOffset.latest()
                ),
                streamConsumer
        );

        container.start();
        return subscription;
    }
}
