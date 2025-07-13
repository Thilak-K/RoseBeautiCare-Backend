package com.rosebeauticare.rosebeauticare.Response.Customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rosebeauticare.rosebeauticare.DTO.CustomerDTO;
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
public class CustomerListResponse {
    private List<CustomerDTO> customers;
    private LocalDateTime timestamp;
    private String requestId;
    private String version;
    private PaginationInfo pagination;
    private SearchInfo searchInfo;

    public static CustomerListResponse of(List<CustomerDTO> customers, String requestId) {
        return CustomerListResponse.builder()
                .customers(customers)
                .timestamp(LocalDateTime.now())
                .requestId(requestId)
                .version("1.0.0")
                .build();
    }

    public static CustomerListResponse withPagination(List<CustomerDTO> customers, 
                                                   int page, int size, long totalElements, int totalPages) {
        return CustomerListResponse.builder()
                .customers(customers)
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

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchInfo {
        private String query;
        private int resultCount;
        private long searchTimeMs;
        private String searchType;
    }
} 