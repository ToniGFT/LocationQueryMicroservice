package com.gft.location_query_microservice.infraestructure.messaging.kafka.strategies;

import reactor.core.publisher.Mono;

public interface EventProcessor<T> {
    Mono<Void> process(String message);
}
