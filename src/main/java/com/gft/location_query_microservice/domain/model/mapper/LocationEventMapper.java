package com.gft.location_query_microservice.domain.model.mapper;

import com.gft.location_query_microservice.domain.events.LocationCreatedEvent;
import com.gft.location_query_microservice.domain.events.LocationUpdatedEvent;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import org.modelmapper.ModelMapper;

public class LocationEventMapper {

    private static final ModelMapper modelMapper = com.gft.location_query_microservice.domain.model.mapper.ModelMapperConfig.getModelMapper();

    private LocationEventMapper() {
    }

    public static LocationUpdate toLocationUpdate(LocationCreatedEvent event) {
        return modelMapper.map(event, LocationUpdate.class);
    }

    public static LocationUpdate toLocationUpdate(LocationUpdatedEvent event, LocationUpdate existingLocation) {
        modelMapper.map(event, existingLocation);
        return existingLocation;
    }
}
