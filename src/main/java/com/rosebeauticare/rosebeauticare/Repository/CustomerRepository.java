package com.rosebeauticare.rosebeauticare.Repository;

import com.rosebeauticare.rosebeauticare.Model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    
    // Basic queries
    List<Customer> findAllByOrderByNameAsc();
    List<Customer> findByNameContainingIgnoreCase(String query);
    boolean existsByPhone(String phone);
    Optional<Customer> findByPhone(String phone);
    
    // Advanced queries with pagination
    Page<Customer> findAllByOrderByNameAsc(Pageable pageable);
    Page<Customer> findByNameContainingIgnoreCase(String query, Pageable pageable);
    
    // Status-based queries
    List<Customer> findByStatus(String status);
    List<Customer> findByStatusOrderByNameAsc(String status);
    long countByStatus(String status);
    
    // Date-based queries
    List<Customer> findByJoinDateBetween(LocalDate startDate, LocalDate endDate);
    List<Customer> findByJoinDateAfter(LocalDate date);
    
    // Complex queries
    @Query("{'$or': [{'name': {'$regex': ?0, '$options': 'i'}}, {'phone': {'$regex': ?0, '$options': 'i'}}]}")
    List<Customer> findByNameOrPhoneContainingIgnoreCase(String query);
    
    @Query("{'$and': [{'status': ?0}, {'joinDate': {'$gte': ?1}}]}")
    List<Customer> findByStatusAndJoinDateAfter(String status, LocalDate date);
    
    // Aggregation queries
    @Query(value = "{}", fields = "{'id': 1, 'name': 1, '_id': 0}")
    List<Customer> findAllBasicInfo();
    
    // Performance optimized queries
    @Query(value = "{'status': 'ACTIVE'}", fields = "{'id': 1, 'name': 1, 'phone': 1}")
    List<Customer> findActiveCustomersBasic();
    
    // Search with multiple criteria
    @Query("{'$and': [{'name': {'$regex': ?0, '$options': 'i'}}, {'status': ?1}]}")
    List<Customer> findByNameContainingAndStatus(String name, String status);
}
