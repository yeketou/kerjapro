package com.kerjapro.contractor.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCertificationRequest {

    @NotBlank(message = "Brand name is required")
    @Size(max = 100)
    private String brandName;

    @NotBlank(message = "Certification name is required")
    @Size(max = 255)
    private String certificationName;

    @Size(max = 100)
    private String certificationNo;

    private LocalDate issuedDate;

    private LocalDate expiryDate;

    private String certificateUrl;
}
