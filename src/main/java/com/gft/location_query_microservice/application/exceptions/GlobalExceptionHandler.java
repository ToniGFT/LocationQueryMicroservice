package com.gft.location_query_microservice.application.exceptions;

import com.gft.location_query_microservice.domain.exception.LocationSaveException;
import jdk.jshell.spi.ExecutionControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private Mono<ResponseEntity<ErrorResponse>> buildErrorResponse(HttpStatus status, String message) {
        log.error("Error occurred: {}", message);  // Loguear el error
        ErrorResponse errorResponse = new ErrorResponse(status.value(), message);
        return Mono.just(ResponseEntity.status(status).body(errorResponse));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        if (e.getMessage().contains("state should be: hexString has 24 characters")) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid ObjectId format. It must be a 24 character hex string.");
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid input: " + e.getMessage());
    }

    @ExceptionHandler(LocationSaveException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleLocationSaveException(LocationSaveException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error saving location update: " + ex.getMessage());
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleHttpMessageConversionException(HttpMessageConversionException e, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format: " + e.getMessage());
    }

    @ExceptionHandler(NumberFormatException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNumberFormatException(NumberFormatException e, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid input: " + e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        for (FieldError fieldError : fieldErrors) {
            errorMessage.append(fieldError.getDefaultMessage()).append("; ");
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage.toString());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNoSuchElementException(NoSuchElementException e, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ExecutionControl.InternalException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInternalException(ExecutionControl.InternalException e, WebRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

}
