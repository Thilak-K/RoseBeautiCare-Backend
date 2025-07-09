package com.rosebeauticare.rosebeauticare.Response.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataResponse<T> {
    private boolean success;
    private T data;
    private String message;
}