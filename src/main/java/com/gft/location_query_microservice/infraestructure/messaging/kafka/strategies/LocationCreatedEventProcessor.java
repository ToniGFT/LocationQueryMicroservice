package com.gft.location_query_microservice.infraestructure.messaging.kafka.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gft.location_query_microservice.application.service.kafka.LocationEventProcessor;
import com.gft.location_query_microservice.domain.events.LocationCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("LOCATION_CREATED")
public class LocationCreatedEventProcessor implements EventProcessor<LocationCreatedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(LocationCreatedEventProcessor.class);

    private final LocationEventProcessor locationEventProcessor;
    private final ObjectMapper objectMapper;

    public LocationCreatedEventProcessor(LocationEventProcessor locationEventProcessor, ObjectMapper objectMapper) {
        this.locationEventProcessor = locationEventProcessor;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> process(String message) {
        logger.info("Starting to process LOCATION_CREATED event. Message: {}", message);
        try {
            LocationCreatedEvent event = objectMapper.readValue(message, LocationCreatedEvent.class);
            return locationEventProcessor.processLocationCreatedEvent(event);
        } catch (Exception e) {
            logger.error("Failed to process LOCATION_CREATED event. Message: {}, Error: {}", message, e.getMessage(), e);
            return Mono.error(new RuntimeException("Failed to process LOCATION_CREATED event", e));
        }
    }
}
