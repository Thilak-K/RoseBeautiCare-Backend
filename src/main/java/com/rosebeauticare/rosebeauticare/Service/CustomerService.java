package com.rosebeauticare.rosebeauticare.Service;

import com.rosebeauticare.rosebeauticare.DTO.CustomerDTO;
import com.rosebeauticare.rosebeauticare.Model.Customer;
import com.rosebeauticare.rosebeauticare.Repository.CustomerRepository;
import com.rosebeauticare.rosebeauticare.Exception.BusinessException;
import com.rosebeauticare.rosebeauticare.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AtomicLong dailyRequestCount = new AtomicLong(0);
    private static final long DAILY_REQUEST_LIMIT = 5000;

    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        checkRequestLimit();
        validatePhoneFormat(customerDTO.getPhone());

        log.info("Creating customer: {}", customerDTO.getName());

        if (customerRepository.existsByPhone(customerDTO.getPhone())) {
            throw new BusinessException("Customer with phone number " + customerDTO.getPhone() + " already exists");
        }

        Customer customer = convertToEntity(customerDTO);
        customer.calculateAge();
        if (customer.getJoinDate() == null) {
            customer.setJoinDate(LocalDate.now());
        }

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created with ID: {}", savedCustomer.getId());
        return convertToDTO(savedCustomer);
    }

    @Cacheable(value = "customers", key = "#id")
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(String id) {
        checkRequestLimit();
        log.debug("Fetching customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        return convertToDTO(customer);
    }

    @Transactional(readOnly = true)
    public List<Map<String, String>> getAllCustomersBasic() {
        checkRequestLimit();
        return customerRepository.findAllBasicInfo().stream()
                .map(customer -> Map.of(
                        "id", customer.getId(),
                        "name", customer.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "customers", key = "#id")
    public CustomerDTO updateCustomer(String id, CustomerDTO customerDTO) {
        checkRequestLimit();
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        updateCustomerFields(existingCustomer, customerDTO);
        existingCustomer.calculateAge();
        
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        log.info("Customer updated successfully: {}", updatedCustomer.getName());
        return convertToDTO(updatedCustomer);
    }

    @Transactional
    @CacheEvict(value = "customers", key = "#id")
    public void deleteCustomer(String id) {
        checkRequestLimit();
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
        log.info("Customer deleted with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> searchCustomers(String query) {
        checkRequestLimit();
        log.debug("Searching customers with query: {}", query);
        return customerRepository.findByNameContainingIgnoreCase(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> searchCustomers(String query, int limit) {
        checkRequestLimit();
        log.debug("Searching customers with query: {}, limit: {}", query, limit);
        return customerRepository.findByNameContainingIgnoreCase(query).stream()
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<Map<String, String>> getAllCustomersPaginated(Pageable pageable) {
        checkRequestLimit();
        log.debug("Fetching customers with pagination: {}", pageable);
        return customerRepository.findAllByOrderByNameAsc(pageable)
                .map(customer -> Map.of(
                        "id", customer.getId(),
                        "name", customer.getName()));
    }

    @Transactional(readOnly = true)
    public long getCustomerCount() {
        checkRequestLimit();
        log.debug("Fetching customer count");
        return customerRepository.count();
    }

    // New optimized methods
    @Transactional(readOnly = true)
    public List<CustomerDTO> getActiveCustomers() {
        checkRequestLimit();
        return customerRepository.findByStatusOrderByNameAsc("ACTIVE").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> searchByNameOrPhone(String query) {
        checkRequestLimit();
        log.debug("Searching customers by name or phone: {}", query);
        return customerRepository.findByNameOrPhoneContainingIgnoreCase(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> getCustomersByJoinDateRange(LocalDate startDate, LocalDate endDate) {
        checkRequestLimit();
        log.debug("Fetching customers by join date range: {} to {}", startDate, endDate);
        return customerRepository.findByJoinDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getCustomerCountByStatus(String status) {
        checkRequestLimit();
        return customerRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> getCustomersByStatus(String status) {
        checkRequestLimit();
        log.debug("Fetching customers with status: {}", status);
        return customerRepository.findByStatusOrderByNameAsc(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private void updateCustomerFields(Customer customer, CustomerDTO dto) {
        if (dto.getPhone() != null) {
            validatePhoneFormat(dto.getPhone());
            if (!dto.getPhone().equals(customer.getPhone()) &&
                    customerRepository.existsByPhone(dto.getPhone())) {
                throw new BusinessException("Phone number " + dto.getPhone() + " is already in use");
            }
            customer.setPhone(dto.getPhone());
        }
        if (dto.getName() != null) customer.setName(dto.getName());
        if (dto.getAltPhone() != null) customer.setAltPhone(dto.getAltPhone());
        if (dto.getAddress() != null) customer.setAddress(dto.getAddress());
        if (dto.getDistrict() != null) customer.setDistrict(dto.getDistrict());
        if (dto.getState() != null) customer.setState(dto.getState());
        if (dto.getStatus() != null) customer.setStatus(dto.getStatus());
        if (dto.getGender() != null) customer.setGender(dto.getGender());
        if (dto.getDob() != null) {
            customer.setDob(dto.getDob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
    }

    private void checkRequestLimit() {
        if (dailyRequestCount.incrementAndGet() > DAILY_REQUEST_LIMIT) {
            throw new BusinessException("Daily request limit of " + DAILY_REQUEST_LIMIT + " exceeded");
        }
    }

    private void validatePhoneFormat(String phone) {
        if (phone == null || !phone.matches("\\d{10}")) {
            throw new BusinessException("Phone number must be exactly 10 digits");
        }
    }

    private Customer convertToEntity(CustomerDTO dto) {
        return Customer.builder()
                .id(dto.getId())
                .name(dto.getName())
                .phone(dto.getPhone())
                .altPhone(dto.getAltPhone())
                .address(dto.getAddress())
                .district(dto.getDistrict())
                .state(dto.getState())
                .status(dto.getStatus())
                .gender(dto.getGender())
                .dob(dto.getDob() != null ? dto.getDob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        : null)
                .joinDate(dto.getJoinDate() != null
                        ? LocalDate.ofInstant(dto.getJoinDate().toInstant(), ZoneId.systemDefault())
                        : null)
                .build();
    }

    private CustomerDTO convertToDTO(Customer entity) {
        return CustomerDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .phone(entity.getPhone())
                .altPhone(entity.getAltPhone())
                .address(entity.getAddress())
                .district(entity.getDistrict())
                .state(entity.getState())
                .status(entity.getStatus())
                .gender(entity.getGender())
                .dob(entity.getDob() != null
                        ? Date.from(entity.getDob().atStartOfDay(ZoneId.systemDefault()).toInstant())
                        : null)
                .age(entity.getAge())
                .joinDate(entity.getJoinDate() != null
                        ? Date.from(entity.getJoinDate().atStartOfDay(ZoneId.systemDefault()).toInstant())
                        : null)
                .build();
    }
}