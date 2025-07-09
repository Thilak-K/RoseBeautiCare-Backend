// File: CustomerController.java (updated methods)
package com.rosebeauticare.rosebeauticare.Controller;

import com.rosebeauticare.rosebeauticare.DTO.CustomerDTO;
import com.rosebeauticare.rosebeauticare.Response.Customer.BasicResponse;
import com.rosebeauticare.rosebeauticare.Response.Customer.DataResponse;
import com.rosebeauticare.rosebeauticare.Response.Customer.IdNameResponse;
import com.rosebeauticare.rosebeauticare.Service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<DataResponse<IdNameResponse>> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        CustomerDTO response = customerService.createCustomer(customerDTO);
        IdNameResponse data = new IdNameResponse(response.getId(), response.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new DataResponse<>(true, data, "Customer created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable String id) {
        CustomerDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getAllCustomers() {
        List<Map<String, String>> customers = customerService.getAllCustomersBasic();
        return ResponseEntity.ok(customers);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BasicResponse> updateCustomer(
            @PathVariable String id, @Valid @RequestBody CustomerDTO customerDTO) {
        customerService.updateCustomer(id, customerDTO);
        return ResponseEntity.ok(new BasicResponse(true, "Customer updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BasicResponse> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(new BasicResponse(true, "Customer deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<DataResponse<List<CustomerDTO>>> searchCustomers(@RequestParam String query) {
        List<CustomerDTO> results = customerService.searchCustomers(query);
        return ResponseEntity.ok(new DataResponse<>(true, results, "Search completed successfully"));
    }
}
