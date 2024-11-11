package com.gft.location_query_microservice.application;


import com.gft.location_query_microservice.application.service.LocationQueryServiceImpl;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.model.valueobject.Coordinates;
import com.gft.location_query_microservice.domain.model.valueobject.enums.Direction;
import com.gft.location_query_microservice.domain.model.valueobject.enums.OperationalStatus;
import com.gft.location_query_microservice.domain.repository.LocationQueryRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LocationQueryEndToEndTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private LocationQueryRepository locationQueryRepository;

    @MockBean
    private LocationQueryServiceImpl locationQueryService;

    private ObjectId vehicleId = ObjectId.get();

    @BeforeEach
    public void setUp() {
        LocationUpdate locationUpdate = LocationUpdate.builder()
                .vehicleId(vehicleId)
                .timestamp(LocalDateTime.now())
                .location(new Coordinates(40.7128, -74.0060))
                .speed(60.5)
                .direction(Direction.NORTH)
                .routeId(new ObjectId())
                .passengerCount(5)
                .status(OperationalStatus.ON_ROUTE)
                .build();

        when(locationQueryRepository.findByVehicleId(any(ObjectId.class)))
                .thenReturn(Mono.just(locationUpdate));

        when(locationQueryService.getLocationByVehicleId(any(ObjectId.class)))
                .thenReturn(Mono.just(locationUpdate));
    }

    @Test
    public void testEndToEnd_GetLocationByVehicleId_ReturnsLocation() {
        webTestClient.get()
                .uri("/locations/{vehicleId}", vehicleId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LocationUpdate.class)
                .value(locationUpdate -> {
                    assert(locationUpdate.getLocation().getLatitude()).equals(40.7128);
                    assert(locationUpdate.getLocation().getLongitude()).equals(-74.0060);
                    assert(locationUpdate.getSpeed()).equals(60.5);
                    assert(locationUpdate.getDirection()).equals(Direction.NORTH);
                    assert(locationUpdate.getStatus()).equals(OperationalStatus.ON_ROUTE);
                });
    }



}
