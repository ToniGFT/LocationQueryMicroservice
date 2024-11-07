package com.gft.location_query_microservice.application.controller;

import com.gft.location_query_microservice.application.service.LocationQueryService;
import com.gft.location_query_microservice.application.service.LocationQueryServiceImpl;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/locations")
@Validated
public class LocationQueryController {

    private static final Logger logger = LoggerFactory.getLogger(LocationQueryController.class);
    private final LocationQueryServiceImpl locationQueryService;

    @Autowired
    public LocationQueryController(LocationQueryServiceImpl locationQueryService) {
        this.locationQueryService = locationQueryService;
    }

    @GetMapping("/{vehicleId}")
    public Mono<ResponseEntity<LocationUpdate>> getLocationByVehicleId(@PathVariable String vehicleId) {
        logger.info("Attempting to retrieve location for vehicle ID: {}", vehicleId);
        return Mono.fromCallable(() -> new ObjectId(vehicleId))
                .flatMap(locationQueryService::getLocationByVehicleId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnSuccess(response -> logger.info("Successfully retrieved location for vehicle ID: {}", vehicleId))
                .doOnError(ex -> logger.error("Error retrieving location for vehicle ID: {}", vehicleId, ex));
    }

}