package com.api.pulsetrack.modules.tracking.config;

import com.api.pulsetrack.modules.tracking.service.TrackingUpdatePublisher;
import com.api.pulsetrack.modules.tracking.service.TrackingUpdateSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@EnableScheduling
public class TrackingPubSubConfig {

    @Bean
    public ChannelTopic trackingUpdateTopic() {
        return new ChannelTopic(TrackingUpdatePublisher.CHANNEL);
    }

    @Bean
    public RedisMessageListenerContainer trackingMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            TrackingUpdateSubscriber subscriber,
            ChannelTopic trackingUpdateTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(subscriber, trackingUpdateTopic);
        return container;
    }
}
