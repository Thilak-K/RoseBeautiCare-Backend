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
public class BasicResponse {
    private boolean success;
    private String message;
    private LocalDateTime timestamp;
    private String requestId;
    private String version;

    public static BasicResponse success(String message) {
        return BasicResponse.builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .version("1.0.0")
                .build();
    }

    public static BasicResponse failure(String message) {
        return BasicResponse.builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .version("1.0.0")
                .build();
    }

    public static BasicResponse success(String message, String requestId) {
        return BasicResponse.builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .requestId(requestId)
                .version("1.0.0")
                .build();
    }
}
