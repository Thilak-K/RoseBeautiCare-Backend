package com.rosebeauticare.rosebeauticare.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDTO {
    // Common fields
    private String id;
    private String name;
    private String phone;
    private String altPhone;
    private String address;
    private String district;
    private String state;
    private String status;
    private String gender;
    private Date dob;
    private Integer age;
    private Date joinDate;
    
    // Response message
    private String message;

    // Factory methods for different response types
    public static CustomerDTO createSuccessResponse(String id, String name) {
        return CustomerDTO.builder()
                .id(id)
                .name(name)
                .message("Operation successful")
                .build();
    }
    
    public static CustomerDTO deleteSuccessResponse(String id) {
        return CustomerDTO.builder()
                .id(id)
                .message("Customer deleted successfully")
                .build();
    }
    
    public static CustomerDTO fullDetailsResponse(CustomerDTO dto) {
        return CustomerDTO.builder()
                .id(dto.getId())
                .name(dto.getName())
                .phone(dto.getPhone())
                .altPhone(dto.getAltPhone())
                .address(dto.getAddress())
                .district(dto.getDistrict())
                .state(dto.getState())
                .status(dto.getStatus())
                .gender(dto.getGender())
                .dob(dto.getDob())
                .age(dto.getAge())
                .joinDate(dto.getJoinDate())
                .message("Customer details retrieved")
                .build();
    }
    
}