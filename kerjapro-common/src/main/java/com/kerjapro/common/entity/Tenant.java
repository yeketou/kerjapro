package com.kerjapro.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tenants", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant extends BaseEntity {

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "logo_url")
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false)
    private Plan plan = Plan.STARTER;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;

    // Generated column — read only
    @Column(name = "schema_name", insertable = false, updatable = false)
    private String schemaName;

    public enum Plan {
        STARTER, PROFESSIONAL, ENTERPRISE
    }

    public enum Status {
        ACTIVE, SUSPENDED, CANCELLED
    }
}
