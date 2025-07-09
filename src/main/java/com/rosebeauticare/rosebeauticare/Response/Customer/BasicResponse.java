package com.rosebeauticare.rosebeauticare.Response.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicResponse {
    private boolean success;
    private String message;
}
