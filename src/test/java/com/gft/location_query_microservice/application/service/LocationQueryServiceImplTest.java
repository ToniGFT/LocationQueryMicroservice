package com.gft.location_query_microservice.application.service;

import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.model.valueobject.Coordinates;
import com.gft.location_query_microservice.domain.model.valueobject.Event;
import com.gft.location_query_microservice.domain.model.valueobject.enums.Direction;
import com.gft.location_query_microservice.domain.model.valueobject.enums.EventType;
import com.gft.location_query_microservice.domain.model.valueobject.enums.OperationalStatus;
import com.gft.location_query_microservice.domain.repository.LocationQueryRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.when;

class LocationQueryServiceImplTest {

    @Mock
    private LocationQueryRepository locationQueryRepository;

    @InjectMocks
    private LocationQueryServiceImpl locationQueryService;

    private ObjectId testVehicleId;
    private LocationUpdate testLocationUpdate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testVehicleId = new ObjectId("672c770a68108027b25f1506");

        Coordinates coordinates = Coordinates.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .build();

        Event event = Event.builder()
                .eventId(new ObjectId())
                .type(EventType.STOP_ARRIVAL)
                .stopId(new ObjectId())
                .timestamp(LocalDateTime.now())
                .details("Arrived at stop")
                .build();

        testLocationUpdate = LocationUpdate.builder()
                .vehicleId(testVehicleId)
                .timestamp(LocalDateTime.now())
                .location(coordinates)
                .speed(60.0)
                .direction(Direction.NORTH)
                .routeId(testVehicleId)
                .passengerCount(10)
                .status(OperationalStatus.ON_ROUTE)
                .events(Arrays.asList(event))
                .build();
    }

    @Test
    void testGetLocationByVehicleId_found() {
        when(locationQueryRepository.findByVehicleId(testVehicleId)).thenReturn(Mono.just(testLocationUpdate));

        StepVerifier.create(locationQueryService.getLocationByVehicleId(testVehicleId))
                .expectNext(testLocationUpdate)
                .verifyComplete();
    }

    @Test
    void testGetLocationByVehicleId_notFound() {
        when(locationQueryRepository.findByVehicleId(testVehicleId)).thenReturn(Mono.empty());

        StepVerifier.create(locationQueryService.getLocationByVehicleId(testVehicleId))
                .verifyComplete();
    }

    @Test
    void testGetLocationByVehicleId_error() {
        when(locationQueryRepository.findByVehicleId(testVehicleId)).thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(locationQueryService.getLocationByVehicleId(testVehicleId))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database error"))
                .verify();
    }
}
