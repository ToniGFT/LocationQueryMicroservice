package com.gft.location_query_microservice.infraestructure.messaging.kafka.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gft.location_query_microservice.application.service.kafka.LocationEventProcessor;
import com.gft.location_query_microservice.domain.events.LocationDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("LOCATION_DELETED")
public class LocationDeletedEventProcessor implements EventProcessor<LocationDeletedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(LocationDeletedEventProcessor.class);

    private final LocationEventProcessor locationEventProcessor;
    private final ObjectMapper objectMapper;

    public LocationDeletedEventProcessor(LocationEventProcessor locationEventProcessor, ObjectMapper objectMapper) {
        this.locationEventProcessor = locationEventProcessor;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> process(String message) {
        logger.info("Starting to process LOCATION_DELETED event. Message: {}", message);
        try {
            LocationDeletedEvent event = objectMapper.readValue(message, LocationDeletedEvent.class);
            return locationEventProcessor.processLocationDeletedEvent(event);
        } catch (Exception e) {
            logger.error("Failed to process LOCATION_DELETED event. Message: {}, Error: {}", message, e.getMessage(), e);
            return Mono.error(new RuntimeException("Failed to process LOCATION_DELETED event", e));
        }
    }
}
