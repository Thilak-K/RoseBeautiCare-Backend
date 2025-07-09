package com.rosebeauticare.rosebeauticare.Model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Builder
@Document(collection = "customers")
public class Customer {
    @Id
    private String id;
    private String name;
    private String phone;
    private String altPhone;
    private String address;
    private String district;
    private String state;
    private String status;
    private String gender;
    private LocalDate dob;
    private Integer age;
    @Builder.Default
    private LocalDate joinDate = LocalDate.now();

    public void calculateAge() {
        if (dob != null) {
            age = LocalDate.now().getYear() - dob.getYear();
        }
    }
}