package com.gft.location_query_microservice.application.exceptions;

import com.gft.location_query_microservice.infraestructure.configuration.JacocoAnnotationGenerated;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JacocoAnnotationGenerated
public class ErrorResponse {
    private int code;
    private String message;
}
