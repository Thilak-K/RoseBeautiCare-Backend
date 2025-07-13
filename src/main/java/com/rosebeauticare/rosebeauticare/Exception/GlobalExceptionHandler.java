package com.rosebeauticare.rosebeauticare.Exception;

import com.rosebeauticare.rosebeauticare.Response.Customer.DataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<DataResponse<ErrorResponse>> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {} - {}", ex.getMessage(), request.getDescription(false));
        
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false)
        );
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setErrorCode("RESOURCE_NOT_FOUND");
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(DataResponse.failure("Resource not found"));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<DataResponse<ErrorResponse>> handleBusinessException(
            BusinessException ex, WebRequest request) {
        log.warn("Business exception: {} - {}", ex.getMessage(), request.getDescription(false));
        
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false)
        );
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setErrorCode("BUSINESS_ERROR");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DataResponse.failure("Business rule violation"));
    }

    @ExceptionHandler(DuplicateCustomerException.class)
    public ResponseEntity<DataResponse<ErrorResponse>> handleDuplicateCustomer(
            DuplicateCustomerException ex, WebRequest request) {
        log.warn("Duplicate customer: {} - Existing ID: {}, Name: {}", 
                ex.getMessage(), ex.getExistingId(), ex.getExistingName());
        
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false)
        );
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setErrorCode("DUPLICATE_CUSTOMER");
        
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(DataResponse.failure("Customer already exists"));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<DataResponse<ErrorResponse>> handleInvalidRequest(
            InvalidRequestException ex, WebRequest request) {
        log.warn("Invalid request: {} - Field: {}, Value: {}", 
                ex.getMessage(), ex.getField(), ex.getValue());
        
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false)
        );
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setErrorCode("INVALID_REQUEST");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DataResponse.failure("Invalid request parameters"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponse<ValidationErrorResponse>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Validation error: {} - {}", ex.getMessage(), request.getDescription(false));
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        String errorMessage = fieldErrors.values().stream()
                .collect(Collectors.joining(", "));
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                "Validation failed",
                request.getDescription(false),
                fieldErrors
        );
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setErrorCode("VALIDATION_ERROR");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DataResponse.failure(errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResponse<ErrorResponse>> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error: {} - {}", ex.getMessage(), request.getDescription(false), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Internal server error occurred",
                request.getDescription(false)
        );
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setErrorCode("INTERNAL_ERROR");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(DataResponse.failure("An unexpected error occurred"));
    }
}