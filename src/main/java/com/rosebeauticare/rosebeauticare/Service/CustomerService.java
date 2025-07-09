package com.rosebeauticare.rosebeauticare.Service;

import com.rosebeauticare.rosebeauticare.DTO.CustomerDTO;
import com.rosebeauticare.rosebeauticare.Model.Customer;
import com.rosebeauticare.rosebeauticare.Repository.CustomerRepository;
import com.rosebeauticare.rosebeauticare.Exception.BusinessException;
import com.rosebeauticare.rosebeauticare.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AtomicLong dailyRequestCount = new AtomicLong(0);
    private static final long DAILY_REQUEST_LIMIT = 5000;

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

    public CustomerDTO getCustomerById(String id) {
        checkRequestLimit();
        log.debug("Fetching customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        return convertToDTO(customer);
    }

    public List<Map<String, String>> getAllCustomersBasic() {
        checkRequestLimit();
        return customerRepository.findAllByOrderByNameAsc().stream()
                .map(customer -> Map.of(
                        "id", customer.getId(),
                        "name", customer.getName()))
                .collect(Collectors.toList());
    }

    public CustomerDTO updateCustomer(String id, CustomerDTO customerDTO) {
        checkRequestLimit();
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        if (customerDTO.getPhone() != null) {
            validatePhoneFormat(customerDTO.getPhone());
            if (!customerDTO.getPhone().equals(existingCustomer.getPhone()) &&
                    customerRepository.existsByPhone(customerDTO.getPhone())) {
                throw new BusinessException("Phone number " + customerDTO.getPhone() + " is already in use");
            }
            existingCustomer.setPhone(customerDTO.getPhone());
        }
        if (customerDTO.getName() != null)
            existingCustomer.setName(customerDTO.getName());
        if (customerDTO.getAltPhone() != null)
            existingCustomer.setAltPhone(customerDTO.getAltPhone());
        if (customerDTO.getAddress() != null)
            existingCustomer.setAddress(customerDTO.getAddress());
        if (customerDTO.getDistrict() != null)
            existingCustomer.setDistrict(customerDTO.getDistrict());
        if (customerDTO.getState() != null)
            existingCustomer.setState(customerDTO.getState());
        if (customerDTO.getStatus() != null)
            existingCustomer.setStatus(customerDTO.getStatus());
        if (customerDTO.getGender() != null)
            existingCustomer.setGender(customerDTO.getGender());
        if (customerDTO.getDob() != null) {
            existingCustomer.setDob(customerDTO.getDob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        existingCustomer.calculateAge();
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return convertToDTO(updatedCustomer);
    }

    public void deleteCustomer(String id) {
        checkRequestLimit();
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
        log.info("Customer deleted with ID: {}", id);
    }

    public List<CustomerDTO> searchCustomers(String query) {
        checkRequestLimit();
        log.debug("Searching customers with query: {}", query);
        return customerRepository.findByNameContainingIgnoreCase(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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