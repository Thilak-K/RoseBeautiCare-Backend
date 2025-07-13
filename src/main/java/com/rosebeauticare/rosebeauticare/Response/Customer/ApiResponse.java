package com.rosebeauticare.rosebeauticare.Response.Customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private LocalDateTime timestamp;
    private String requestId;
    private String version;
    private String endpoint;
    private String method;

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .version("1.0.0")
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message, String requestId) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .requestId(requestId)
                .version("1.0.0")
                .build();
    }

    public static <T> ApiResponse<T> failure(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .version("1.0.0")
                .build();
    }

    public static <T> ApiResponse<T> failure(String message, String requestId) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .requestId(requestId)
                .version("1.0.0")
                .build();
    }

    public static <T> ApiResponse<T> withEndpoint(T data, String message, String endpoint, String method) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .version("1.0.0")
                .endpoint(endpoint)
                .method(method)
                .build();
    }
} 