package com.kerjapro.contractor.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class BrandCertificationDto {
    private UUID id;
    private String brandName;
    private String certificationName;
    private String certificationNo;
    private LocalDate issuedDate;
    private LocalDate expiryDate;
    private String certificateUrl;
    private boolean verified;
    private boolean expired;
}
