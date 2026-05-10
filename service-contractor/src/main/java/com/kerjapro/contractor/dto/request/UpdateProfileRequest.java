package com.kerjapro.contractor.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(min = 2, max = 255)
    private String businessName;

    @Size(max = 255)
    private String displayName;

    @Size(max = 1000)
    private String bio;

    @Size(max = 30)
    private String phone;

    private String city;
    private String state;

    @Size(max = 10)
    private String cidbGrade;

    @Size(max = 100)
    private String cidbRegistrationNo;

    private Boolean available;
}
