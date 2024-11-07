package com.gft.location_query_microservice.application.exceptions;

import com.gft.location_query_microservice.domain.exception.LocationSaveException;
import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @Mock
    private WebRequest webRequest;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleIllegalArgumentException_withInvalidObjectIdFormat() {
        IllegalArgumentException exception = new IllegalArgumentException("state should be: hexString has 24 characters");

        Mono<ResponseEntity<ErrorResponse>> response = globalExceptionHandler.handleIllegalArgumentException(exception, webRequest);

        ErrorResponse expectedError = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid ObjectId format. It must be a 24 character hex string.");
        assertEquals(expectedError.getCode(), response.block().getStatusCode().value());
        assertEquals(expectedError.getMessage(), response.block().getBody().getMessage());
    }

    @Test
    void testHandleIllegalArgumentException_withOtherMessage() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid input");

        Mono<ResponseEntity<ErrorResponse>> response = globalExceptionHandler.handleIllegalArgumentException(exception, webRequest);

        ErrorResponse expectedError = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid input: Invalid input");
        assertEquals(expectedError.getCode(), response.block().getStatusCode().value());
        assertEquals(expectedError.getMessage(), response.block().getBody().getMessage());
    }

    @Test
    void testHandleLocationSaveException() {
        LocationSaveException exception = new LocationSaveException("Error saving location update");

        Mono<ResponseEntity<ErrorResponse>> response = globalExceptionHandler.handleLocationSaveException(exception);

        ErrorResponse expectedError = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error saving location update: Error saving location update");
        assertEquals(expectedError.getCode(), response.block().getStatusCode().value());
        assertEquals(expectedError.getMessage(), response.block().getBody().getMessage());
    }

    @Test
    void testHandleHttpMessageConversionException() {
        HttpMessageConversionException exception = new HttpMessageConversionException("Invalid JSON format");

        Mono<ResponseEntity<ErrorResponse>> response = globalExceptionHandler.handleHttpMessageConversionException(exception, webRequest);

        ErrorResponse expectedError = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid JSON format: Invalid JSON format");
        assertEquals(expectedError.getCode(), response.block().getStatusCode().value());
        assertEquals(expectedError.getMessage(), response.block().getBody().getMessage());
    }

    @Test
    void testHandleMethodArgumentNotValidException() {
        FieldError fieldError = new FieldError("object", "field", "must not be null");
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        Mono<ResponseEntity<ErrorResponse>> response = globalExceptionHandler.handleMethodArgumentNotValid(exception);

        ErrorResponse expectedError = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed: must not be null; ");
        assertEquals(expectedError.getCode(), response.block().getStatusCode().value());
        assertEquals(expectedError.getMessage(), response.block().getBody().getMessage());
    }

    @Test
    void testHandleNoSuchElementException() {
        NoSuchElementException exception = new NoSuchElementException("No element found");

        Mono<ResponseEntity<ErrorResponse>> response = globalExceptionHandler.handleNoSuchElementException(exception, webRequest);

        ErrorResponse expectedError = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "No element found");
        assertEquals(expectedError.getCode(), response.block().getStatusCode().value());
        assertEquals(expectedError.getMessage(), response.block().getBody().getMessage());
    }

    @Test
    void testHandleInternalException() {
        ExecutionControl.InternalException exception = new ExecutionControl.InternalException("Internal server error");

        Mono<ResponseEntity<ErrorResponse>> response = globalExceptionHandler.handleInternalException(exception, webRequest);

        ErrorResponse expectedError = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error");
        assertEquals(expectedError.getCode(), response.block().getStatusCode().value());
        assertEquals(expectedError.getMessage(), response.block().getBody().getMessage());
    }

}
