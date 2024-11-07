package com.gft.location_query_microservice.application.controller;

import com.gft.location_query_microservice.application.service.LocationQueryService;
import com.gft.location_query_microservice.application.service.LocationQueryServiceImpl;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.model.valueobject.Coordinates;
import com.gft.location_query_microservice.domain.model.valueobject.enums.Direction;
import com.gft.location_query_microservice.domain.model.valueobject.enums.OperationalStatus;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationQueryControllerTest {

    @Mock
    private LocationQueryServiceImpl locationQueryService;

    @InjectMocks
    private LocationQueryController locationQueryController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(locationQueryController)
                .configureClient()
                .baseUrl("http://localhost")
                .build();
    }

    @Test
    void testGetLocationByVehicleId_Success() {
        ObjectId vehicleId = new ObjectId("672b476c26af874dd99c9cfa");
        LocationUpdate locationUpdate = LocationUpdate.builder()
                .vehicleId(vehicleId)
                .timestamp(LocalDateTime.now())
                .location(new Coordinates(40.7128, -74.0060))
                .speed(50.0)
                .direction(Direction.NORTH)
                .routeId(new ObjectId())
                .passengerCount(10)
                .status(OperationalStatus.ON_ROUTE)
                .events(List.of())
                .build();

        when(locationQueryService.getLocationByVehicleId(vehicleId))
                .thenReturn(Mono.just(locationUpdate));

        webTestClient.get()
                .uri("/locations/{vehicleId}", vehicleId.toString())
                .exchange()
                .expectStatus().isOk()
                .expectBody(LocationUpdate.class)
                .value(response -> {
                    assert response.getLocation().getLatitude().equals(40.7128);
                    assert response.getLocation().getLongitude().equals(-74.0060);
                    assert response.getSpeed().equals(50.0);
                    assert response.getStatus().equals(OperationalStatus.ON_ROUTE);
                });
    }

    @Test
    void testGetLocationByVehicleId_NotFound() {
        ObjectId vehicleId = new ObjectId();

        when(locationQueryService.getLocationByVehicleId(vehicleId))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/locations/{vehicleId}", vehicleId.toString())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

}
