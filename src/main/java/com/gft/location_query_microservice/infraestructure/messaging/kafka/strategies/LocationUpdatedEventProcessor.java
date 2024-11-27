package com.gft.location_query_microservice.infraestructure.messaging.kafka.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gft.location_query_microservice.application.service.kafka.LocationEventProcessor;
import com.gft.location_query_microservice.domain.events.LocationUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("LOCATION_UPDATED")
public class LocationUpdatedEventProcessor implements EventProcessor<LocationUpdatedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(LocationUpdatedEventProcessor.class);

    private final LocationEventProcessor locationEventProcessor;
    private final ObjectMapper objectMapper;

    public LocationUpdatedEventProcessor(LocationEventProcessor locationEventProcessor, ObjectMapper objectMapper) {
        this.locationEventProcessor = locationEventProcessor;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> process(String message) {
        logger.info("Starting to process LOCATION_UPDATED event. Message: {}", message);
        try {
            LocationUpdatedEvent event = objectMapper.readValue(message, LocationUpdatedEvent.class);
            return locationEventProcessor.processLocationUpdatedEvent(event);
        } catch (Exception e) {
            logger.error("Failed to process LOCATION_UPDATED event. Message: {}, Error: {}", message, e.getMessage(), e);
            return Mono.error(new RuntimeException("Failed to process LOCATION_UPDATED event", e));
        }
    }
}
