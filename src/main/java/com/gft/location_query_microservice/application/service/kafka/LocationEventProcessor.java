package com.gft.location_query_microservice.application.service.kafka;

import com.gft.location_query_microservice.domain.events.LocationCreatedEvent;
import com.gft.location_query_microservice.domain.events.LocationDeletedEvent;
import com.gft.location_query_microservice.domain.events.LocationUpdatedEvent;
import reactor.core.publisher.Mono;

public interface LocationEventProcessor {
    Mono<Void> processLocationCreatedEvent(LocationCreatedEvent event);

    Mono<Void> processLocationDeletedEvent(LocationDeletedEvent event);

    Mono<Void> processLocationUpdatedEvent(LocationUpdatedEvent event);
}
