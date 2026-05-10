package com.kerjapro.contractor.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateProfileRequest {

    @NotBlank(message = "Business name is required")
    @Size(min = 2, max = 255)
    private String businessName;

    @Size(max = 255)
    private String displayName;

    @Size(max = 1000)
    private String bio;

    @NotBlank(message = "Phone is required")
    @Size(max = 30)
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email required")
    private String email;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @Size(max = 10)
    private String cidbGrade;

    @Size(max = 100)
    private String cidbRegistrationNo;
}
