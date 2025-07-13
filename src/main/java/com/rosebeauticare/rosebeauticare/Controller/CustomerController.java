// File: CustomerController.java (updated methods)
package com.rosebeauticare.rosebeauticare.Controller;

import com.rosebeauticare.rosebeauticare.DTO.CustomerDTO;
import com.rosebeauticare.rosebeauticare.Response.Customer.BasicResponse;
import com.rosebeauticare.rosebeauticare.Response.Customer.DataResponse;
import com.rosebeauticare.rosebeauticare.Response.Customer.IdNameResponse;
import com.rosebeauticare.rosebeauticare.Service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Validated
@Tag(name = "Customer Management", description = "APIs for managing customer data")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create a new customer", description = "Creates a new customer with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer created successfully",
            content = @Content(schema = @Schema(implementation = DataResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Customer with phone number already exists")
    })
    public ResponseEntity<DataResponse<IdNameResponse>> createCustomer(
            @Valid @RequestBody CustomerDTO customerDTO) {
        log.info("Creating customer: {}", customerDTO.getName());
        
        CustomerDTO response = customerService.createCustomer(customerDTO);
        IdNameResponse data = IdNameResponse.fromCustomer(response.getId(), response.getName());
        
        log.info("Customer created successfully with ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResponse.success(data, "Customer created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieves customer details by their unique ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer found",
            content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<DataResponse<CustomerDTO>> getCustomerById(
            @Parameter(description = "Customer ID", example = "CUST001")
            @PathVariable @NotBlank(message = "Customer ID is required") String id) {
        log.debug("Fetching customer with ID: {}", id);
        
        CustomerDTO customer = customerService.getCustomerById(id);
        
        log.debug("Customer found: {}", customer.getName());
        return ResponseEntity.ok(DataResponse.success(customer, "Customer retrieved successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieves a paginated list of all customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customers retrieved successfully")
    })
    public ResponseEntity<DataResponse<Page<Map<String, String>>>> getAllCustomers(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field", example = "name")
            @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction", example = "ASC")
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        log.debug("Fetching customers - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Map<String, String>> customers = customerService.getAllCustomersPaginated(pageable);
        
        log.debug("Retrieved {} customers", customers.getTotalElements());
        return ResponseEntity.ok(DataResponse.success(customers, "Customers retrieved successfully"));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update customer", description = "Updates an existing customer's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Phone number already in use")
    })
    public ResponseEntity<DataResponse<CustomerDTO>> updateCustomer(
            @Parameter(description = "Customer ID", example = "CUST001")
            @PathVariable @NotBlank(message = "Customer ID is required") String id,
            @Valid @RequestBody CustomerDTO customerDTO) {
        log.info("Updating customer with ID: {}", id);
        
        CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
        
        log.info("Customer updated successfully: {}", updatedCustomer.getName());
        return ResponseEntity.ok(DataResponse.success(updatedCustomer, "Customer updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Deletes a customer by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<BasicResponse> deleteCustomer(
            @Parameter(description = "Customer ID", example = "CUST001")
            @PathVariable @NotBlank(message = "Customer ID is required") String id) {
        log.info("Deleting customer with ID: {}", id);
        
        customerService.deleteCustomer(id);
        
        log.info("Customer deleted successfully with ID: {}", id);
        return ResponseEntity.ok(BasicResponse.success("Customer deleted successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search customers", description = "Searches customers by name using case-insensitive matching")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<DataResponse<List<CustomerDTO>>> searchCustomers(
            @Parameter(description = "Search query", example = "john")
            @RequestParam @NotBlank(message = "Search query is required") String query,
            @Parameter(description = "Maximum results", example = "50")
            @RequestParam(defaultValue = "50") int limit) {
        
        log.debug("Searching customers with query: '{}', limit: {}", query, limit);
        
        List<CustomerDTO> results = customerService.searchCustomers(query, limit);
        
        log.debug("Search completed, found {} customers", results.size());
        return ResponseEntity.ok(DataResponse.success(results,
                "Search completed successfully. Found %d customers.".formatted(results.size())));
    }

    @GetMapping("/{id}/basic")
    @Operation(summary = "Get customer basic info", description = "Retrieves basic customer information (ID and name only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer basic info retrieved"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<DataResponse<IdNameResponse>> getCustomerBasicInfo(
            @Parameter(description = "Customer ID", example = "CUST001")
            @PathVariable @NotBlank(message = "Customer ID is required") String id) {
        log.debug("Fetching basic info for customer ID: {}", id);
        
        CustomerDTO customer = customerService.getCustomerById(id);
        IdNameResponse basicInfo = IdNameResponse.fromCustomer(customer.getId(), customer.getName());
        
        return ResponseEntity.ok(DataResponse.success(basicInfo, "Customer basic info retrieved successfully"));
    }

    @GetMapping("/count")
    @Operation(summary = "Get customer count", description = "Returns the total number of customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer count retrieved")
    })
    public ResponseEntity<DataResponse<Long>> getCustomerCount() {
        log.debug("Fetching customer count");
        
        long count = customerService.getCustomerCount();
        
        log.debug("Total customer count: {}", count);
        return ResponseEntity.ok(DataResponse.success(count, "Customer count retrieved successfully"));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active customers", description = "Retrieves all active customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active customers retrieved successfully")
    })
    public ResponseEntity<DataResponse<List<CustomerDTO>>> getActiveCustomers() {
        log.debug("Fetching active customers");
        
        List<CustomerDTO> activeCustomers = customerService.getActiveCustomers();
        
        log.debug("Found {} active customers", activeCustomers.size());
        return ResponseEntity.ok(DataResponse.success(activeCustomers,
                "Found %d active customers".formatted(activeCustomers.size())));
    }

    @GetMapping("/search/advanced")
    @Operation(summary = "Advanced search", description = "Searches customers by name or phone number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<DataResponse<List<CustomerDTO>>> advancedSearch(
            @Parameter(description = "Search query", example = "john")
            @RequestParam @NotBlank(message = "Search query is required") String query) {
        
        log.debug("Advanced search with query: '{}'", query);
        
        List<CustomerDTO> results = customerService.searchByNameOrPhone(query);
        
        log.debug("Advanced search completed, found {} customers", results.size());
        return ResponseEntity.ok(DataResponse.success(results,
                "Advanced search completed. Found %d customers.".formatted(results.size())));
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get customers by status", description = "Retrieves customers filtered by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customers retrieved successfully")
    })
    public ResponseEntity<DataResponse<List<CustomerDTO>>> getCustomersByStatus(
            @Parameter(description = "Customer status", example = "ACTIVE")
            @PathVariable @NotBlank(message = "Status is required") String status) {
        
        log.debug("Fetching customers with status: {}", status);
        
        List<CustomerDTO> customers = customerService.getCustomersByStatus(status);
        
        log.debug("Found {} customers with status: {}", customers.size(), status);
        return ResponseEntity.ok(DataResponse.success(customers,
                "Found %d customers with status: %s".formatted(customers.size(), status)));
    }

    @GetMapping("/count/by-status/{status}")
    @Operation(summary = "Get customer count by status", description = "Returns the count of customers by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer count retrieved")
    })
    public ResponseEntity<DataResponse<Long>> getCustomerCountByStatus(
            @Parameter(description = "Customer status", example = "ACTIVE")
            @PathVariable @NotBlank(message = "Status is required") String status) {
        
        log.debug("Fetching customer count for status: {}", status);
        
        long count = customerService.getCustomerCountByStatus(status);
        
        log.debug("Found {} customers with status: {}", count, status);
        return ResponseEntity.ok(DataResponse.success(count,
                "Found %d customers with status: %s".formatted(count, status)));
    }
}
