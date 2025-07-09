package com.rosebeauticare.rosebeauticare.Controller;

import com.rosebeauticare.rosebeauticare.DTO.CustomerDTO;
import com.rosebeauticare.rosebeauticare.Service.CustomerService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Data
    static class ResponseWrapper<T> {
        private final boolean success;
        private final T data;
        private final String message;
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<CustomerDTO>> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        CustomerDTO response = customerService.createCustomer(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseWrapper<>(true, response, "Customer created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<CustomerDTO>> getCustomerById(@PathVariable String id) {
        CustomerDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(new ResponseWrapper<>(true, customer, "Customer retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<Map<String, String>>>> getAllCustomers() {
        List<Map<String, String>> customers = customerService.getAllCustomersBasic();
        return ResponseEntity.ok(new ResponseWrapper<>(true, customers, "Customers retrieved successfully"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseWrapper<CustomerDTO>> updateCustomer(
            @PathVariable String id, @Valid @RequestBody CustomerDTO customerDTO) {
        CustomerDTO response = customerService.updateCustomer(id, customerDTO);
        return ResponseEntity.ok(new ResponseWrapper<>(true, response, "Customer updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(new ResponseWrapper<>(true, null, "Customer deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseWrapper<List<CustomerDTO>>> searchCustomers(@RequestParam String query) {
        List<CustomerDTO> results = customerService.searchCustomers(query);
        return ResponseEntity.ok(new ResponseWrapper<>(true, results, "Search completed successfully"));
    }
}