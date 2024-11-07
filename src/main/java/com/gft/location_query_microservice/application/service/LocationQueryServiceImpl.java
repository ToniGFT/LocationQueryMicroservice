package com.gft.location_query_microservice.application.service;

import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.repository.LocationQueryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LocationQueryServiceImpl implements LocationQueryService {

    private final LocationQueryRepository locationQueryRepository;

    @Autowired
    public LocationQueryServiceImpl(LocationQueryRepository locationQueryRepository) {
        this.locationQueryRepository = locationQueryRepository;
    }

    @Override
    public Mono<LocationUpdate> getLocationByVehicleId(ObjectId vehicleId) {
        return locationQueryRepository.findByVehicleId(vehicleId);
    }
}
