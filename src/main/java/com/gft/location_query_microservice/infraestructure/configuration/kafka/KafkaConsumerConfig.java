package com.gft.location_query_microservice.infraestructure.configuration.kafka;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${kafka.topics.location-events}")
    private String locationEventsTopic;

    @PostConstruct
    public void logConfig() {
        logger.info("Kafka Consumer Config - Bootstrap Servers: {}", bootstrapServers);
        logger.info("Kafka Consumer Config - Group ID: {}", groupId);
        logger.info("Kafka Consumer Config - Topic: {}", locationEventsTopic);
    }

    @Bean
    public ReceiverOptions<String, String> receiverOptions() {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);
        consumerProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);
        consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);

        return ReceiverOptions.<String, String>create(consumerProps)
                .subscription(List.of(locationEventsTopic));
    }
}
