package com.gft.location_query_microservice.application.service;

import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

public interface LocationQueryService {

    Mono<LocationUpdate> getLocationByVehicleId(ObjectId vehicleId);
}
