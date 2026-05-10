package com.kerjapro.contractor.entity;

import com.kerjapro.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "brand_certifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandCertification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private SubcontractorProfile profile;

    @Column(name = "brand_name", nullable = false)
    private String brandName; // e.g. "Grohe", "American Standard", "Panasonic"

    @Column(name = "certification_name", nullable = false)
    private String certificationName; // e.g. "Certified Grohe Installer"

    @Column(name = "certification_no")
    private String certificationNo;

    @Column(name = "issued_date")
    private LocalDate issuedDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "certificate_url")
    private String certificateUrl;

    @Column(name = "is_verified", nullable = false)
    private boolean verified = false; // verified by brand/platform
}
