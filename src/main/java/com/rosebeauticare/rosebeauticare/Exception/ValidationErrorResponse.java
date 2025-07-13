package com.rosebeauticare.rosebeauticare.Exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> fieldErrors;

    public ValidationErrorResponse(String message, String details, LocalDateTime timestamp, String errorCode, Map<String, String> fieldErrors) {
        super(message, details, timestamp, errorCode);
        this.fieldErrors = fieldErrors;
    }

    public ValidationErrorResponse(String message, String details, Map<String, String> fieldErrors) {
        super(message, details);
        this.fieldErrors = fieldErrors;
    }
}