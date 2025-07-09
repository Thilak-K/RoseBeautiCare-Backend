package com.rosebeauticare.rosebeauticare.Exception;

public class DuplicateCustomerException extends BusinessException {
    private final String existingId;
    private final String existingName;

    public DuplicateCustomerException(String message, String existingId, String existingName) {
        super(message);
        this.existingId = existingId;
        this.existingName = existingName;
    }

    public String getExistingId() {
        return existingId;
    }

    public String getExistingName() {
        return existingName;
    }
}