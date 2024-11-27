package com.gft.location_query_microservice.application.service.kafka;

import com.gft.location_query_microservice.domain.events.LocationCreatedEvent;
import com.gft.location_query_microservice.domain.events.LocationDeletedEvent;
import com.gft.location_query_microservice.domain.events.LocationUpdatedEvent;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.model.mapper.LocationEventMapper;
import com.gft.location_query_microservice.infraestructure.repository.LocationCommandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LocationEventProcessorImpl implements LocationEventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(LocationEventProcessorImpl.class);

    private final LocationCommandRepository locationCommandRepository;

    public LocationEventProcessorImpl(LocationCommandRepository locationCommandRepository) {
        this.locationCommandRepository = locationCommandRepository;
    }

    @Override
    public Mono<Void> processLocationCreatedEvent(LocationCreatedEvent event) {
        logger.info("Processing LocationCreatedEvent: {}", event);
        LocationUpdate locationUpdate = LocationEventMapper.toLocationUpdate(event);
        logger.debug("Mapped LocationCreatedEvent to LocationUpdate: {}", locationUpdate);

        return locationCommandRepository.save(locationUpdate)
                .doOnSuccess(savedLocation -> logger.info("Successfully saved location update to database: {}", savedLocation))
                .doOnError(error -> logger.error("Failed to save location update to database: {}", error.getMessage(), error))
                .then();
    }

    @Override
    public Mono<Void> processLocationDeletedEvent(LocationDeletedEvent event) {
        logger.info("Processing LocationDeletedEvent: {}", event);
        return locationCommandRepository.deleteById(event.getVehicleId())
                .doOnSuccess(unused -> logger.info("Successfully deleted location update with vehicle ID: {}", event.getVehicleId()))
                .doOnError(error -> logger.error("Failed to delete location update with vehicle ID: {}", event.getVehicleId(), error))
                .then();
    }

    @Override
    public Mono<Void> processLocationUpdatedEvent(LocationUpdatedEvent event) {
        logger.info("Processing LocationUpdatedEvent: {}", event);
        return locationCommandRepository.findById(event.getVehicleId())
                .doOnSubscribe(subscription -> logger.debug("Fetching location update with vehicle ID: {}", event.getVehicleId()))
                .doOnNext(existingLocation -> logger.debug("Found existing location update: {}", existingLocation))
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("No location update found with vehicle ID: {}", event.getVehicleId());
                    return Mono.empty();
                }))
                .flatMap(existingLocation -> {
                    LocationUpdate updatedLocation = LocationEventMapper.toLocationUpdate(event, existingLocation);
                    logger.debug("Mapped LocationUpdatedEvent to updated LocationUpdate: {}", updatedLocation);
                    return locationCommandRepository.save(updatedLocation)
                            .doOnSuccess(savedLocation -> logger.info("Successfully updated location update in database: {}", savedLocation));
                })
                .doOnError(error -> logger.error("Failed to update location update with vehicle ID: {}", event.getVehicleId(), error))
                .then();
    }
}

