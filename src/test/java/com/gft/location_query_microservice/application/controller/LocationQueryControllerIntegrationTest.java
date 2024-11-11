package com.gft.location_query_microservice.application.controller;

import com.gft.location_query_microservice.application.service.LocationQueryServiceImpl;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.model.valueobject.Coordinates;
import com.gft.location_query_microservice.domain.model.valueobject.enums.Direction;
import com.gft.location_query_microservice.domain.model.valueobject.enums.OperationalStatus;
import com.gft.location_query_microservice.domain.repository.LocationQueryRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LocationQueryControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private LocationQueryServiceImpl locationQueryService;

    @MockBean
    private LocationQueryRepository locationQueryRepository;

    private ObjectId vehicleId = ObjectId.get();

    @BeforeEach
    public void setUp() {

        LocationUpdate locationUpdate = LocationUpdate.builder()
                .vehicleId(new ObjectId(String.valueOf(vehicleId)))
                .timestamp(LocalDateTime.now())
                .location(new Coordinates(40.7128, -74.0060))
                .speed(60.5)
                .direction(Direction.NORTH)
                .routeId(new ObjectId())
                .passengerCount(5)
                .status(OperationalStatus.ON_ROUTE)
                .build();

        Mockito.when(locationQueryRepository.findByVehicleId(Mockito.any(ObjectId.class)))
                .thenReturn(Mono.just(locationUpdate));
    }

    @Test
    public void testGetLocationByVehicleId_ReturnsLocation() {
        webTestClient.get()
                .uri("/locations/{vehicleId}", vehicleId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LocationUpdate.class)
                .value(locationUpdate -> {
                    assertEquals(40.7128, locationUpdate.getLocation().getLatitude());
                    assertEquals(-74.0060, locationUpdate.getLocation().getLongitude());
                    assertEquals(60.5, locationUpdate.getSpeed());
                });
    }

    @Test
    public void testGetLocationByVehicleId_NotFound() {
        String invalidVehicleId = new ObjectId().toString();

        Mockito.when(locationQueryRepository.findByVehicleId(Mockito.any(ObjectId.class)))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/locations/{vehicleId}", invalidVehicleId)
                .exchange()
                .expectStatus().isNotFound();
    }
}
