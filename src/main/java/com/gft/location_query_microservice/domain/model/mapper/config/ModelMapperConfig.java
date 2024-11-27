package com.gft.location_query_microservice.domain.model.mapper.config;

import com.gft.location_query_microservice.domain.events.LocationCreatedEvent;
import com.gft.location_query_microservice.domain.events.LocationUpdatedEvent;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

public class ModelMapperConfig {

    @Getter
    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.addMappings(new PropertyMap<LocationUpdatedEvent, LocationUpdate>() {
            @Override
            protected void configure() {
                map(source.getVehicleId(), destination.getVehicleId());
                map(source.getRouteId(), destination.getRouteId());
            }
        });

        modelMapper.addMappings(new PropertyMap<LocationCreatedEvent, LocationUpdate>() {
            @Override
            protected void configure() {
                map(source.getVehicleId(), destination.getVehicleId());
                map(source.getRouteId(), destination.getRouteId());
            }
        });
    }

    private ModelMapperConfig() {
    }
}

