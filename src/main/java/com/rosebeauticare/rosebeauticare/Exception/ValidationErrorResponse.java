package com.rosebeauticare.rosebeauticare.Exception;

import java.util.List;

public class ValidationErrorResponse extends ErrorResponse {
    private List<String> errors;

    public ValidationErrorResponse(String message, String details, List<String> errors) {
        super(message, details);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}