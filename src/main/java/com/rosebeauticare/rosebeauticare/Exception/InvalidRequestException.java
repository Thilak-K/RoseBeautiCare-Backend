package com.rosebeauticare.rosebeauticare.Exception;

public class InvalidRequestException extends BusinessException {
    private final String field;
    private final String value;

    public InvalidRequestException(String message, String field, String value) {
        super(message);
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
} 