package com.gft.location_query_microservice.domain.model.mapper;

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
                skip(destination.getVehicleId());
            }
        });
    }

    private ModelMapperConfig() {
    }
}
