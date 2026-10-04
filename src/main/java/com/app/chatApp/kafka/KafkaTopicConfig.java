package com.app.chatApp.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic chatTopic() {
        return TopicBuilder.name("chat-messages")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic emailOtpTopic() {
        return TopicBuilder.name("email-otp")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
