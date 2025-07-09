package com.rosebeauticare.rosebeauticare.Repository;

import com.rosebeauticare.rosebeauticare.Model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    List<Customer> findAllByOrderByNameAsc();
    List<Customer> findByNameContainingIgnoreCase(String query);
    boolean existsByPhone(String phone);
    Optional<Customer> findByPhone(String phone);
}
