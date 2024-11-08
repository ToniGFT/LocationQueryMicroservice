package com.gft.location_query_microservice.domain.repository;

import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.infraestructure.configuration.JacocoAnnotationGenerated;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@JacocoAnnotationGenerated
public interface LocationQueryRepository extends ReactiveMongoRepository<LocationUpdate, ObjectId> {

    Mono<LocationUpdate> findByVehicleId(ObjectId vehicleId);

}

