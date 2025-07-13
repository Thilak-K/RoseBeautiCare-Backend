package com.rosebeauticare.rosebeauticare.Response.Customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdNameResponse {
    @NotBlank(message = "ID is required")
    private String id;
    
    @NotBlank(message = "Name is required")
    private String name;

    public static IdNameResponse of(String id, String name) {
        return IdNameResponse.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static IdNameResponse fromCustomer(String id, String name) {
        return IdNameResponse.builder()
                .id(id)
                .name(name != null ? name.trim() : null)
                .build();
    }
}