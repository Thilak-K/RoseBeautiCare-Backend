package com.rosebeauticare.rosebeauticare.Response.Customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private LocalDateTime timestamp;
    private String requestId;
    private String version;
    private PaginationInfo pagination;

    public static <T> DataResponse<T> success(T data, String message) {
        return DataResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .version("1.0.0")
                .build();
    }

    public static <T> DataResponse<T> success(T data, String message, String requestId) {
        return DataResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .requestId(requestId)
                .version("1.0.0")
                .build();
    }

    public static <T> DataResponse<T> failure(String message) {
        return DataResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .version("1.0.0")
                .build();
    }

    public static <T> DataResponse<List<T>> successWithPagination(List<T> data, String message, 
                                                                 int page, int size, long totalElements, int totalPages) {
        return DataResponse.<List<T>>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .version("1.0.0")
                .pagination(PaginationInfo.builder()
                        .page(page)
                        .size(size)
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaginationInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public boolean isHasNext() {
            return page < totalPages - 1;
        }

        public boolean isHasPrevious() {
            return page > 0;
        }
    }
}