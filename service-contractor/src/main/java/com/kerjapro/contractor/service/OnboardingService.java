package com.kerjapro.contractor.service;

import com.kerjapro.common.entity.Tenant;
import com.kerjapro.common.exception.BusinessException;
import com.kerjapro.common.tenant.TenantSchemaService;
import com.kerjapro.contractor.dto.request.CreateProfileRequest;
import com.kerjapro.contractor.dto.response.SubcontractorProfileDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final SubcontractorProfileService profileService;
    private final TenantSchemaService         tenantSchemaService;
    private final EntityManager               entityManager;

    // ─────────────────────────────────────────────
    // Subcontractor onboarding
    // ─────────────────────────────────────────────

    @Transactional
    public SubcontractorProfileDto onboardSubcontractor(String userId, CreateProfileRequest request) {
        return profileService.createProfile(userId, request);
    }

    // ─────────────────────────────────────────────
    // Main contractor / tenant onboarding
    // ─────────────────────────────────────────────

    @Transactional
    public TenantOnboardingResponse onboardTenant(String userId, TenantOnboardingRequest request) {
        // Check slug uniqueness
        Long count = (Long) entityManager
                .createNativeQuery("SELECT COUNT(*) FROM public.tenants WHERE slug = :slug")
                .setParameter("slug", request.getSlug())
                .getSingleResult();

        if (count > 0) {
            throw new BusinessException(
                "Workspace ID '" + request.getSlug() + "' is already taken. Please choose another.",
                HttpStatus.CONFLICT, "SLUG_TAKEN");
        }

        // Persist tenant record in public schema
        entityManager.createNativeQuery("""
            INSERT INTO public.tenants (slug, company_name, email, phone, plan, status)
            VALUES (:slug, :companyName, :email, :phone, :plan, 'ACTIVE')
            """)
            .setParameter("slug",        request.getSlug())
            .setParameter("companyName", request.getCompanyName())
            .setParameter("email",       request.getEmail())
            .setParameter("phone",       request.getPhone())
            .setParameter("plan",        request.getPlan())
            .executeUpdate();

        // Update user's tenant_id
        entityManager.createNativeQuery("""
            UPDATE public.users SET tenant_id = (
                SELECT id FROM public.tenants WHERE slug = :slug
            ) WHERE keycloak_id = :userId
            """)
            .setParameter("slug",   request.getSlug())
            .setParameter("userId", userId)
            .executeUpdate();

        // Provision tenant schema + run migrations
        tenantSchemaService.provisionTenantSchema(request.getSlug());

        log.info("Tenant onboarded: slug={}, plan={}, userId={}", request.getSlug(), request.getPlan(), userId);
        return new TenantOnboardingResponse(request.getSlug(), "tenant_" + request.getSlug());
    }

    // ─────────────────────────────────────────────
    // Request / Response inner types
    // ─────────────────────────────────────────────

    @Data
    public static class TenantOnboardingRequest {
        @NotBlank private String companyName;
        @NotBlank @Pattern(regexp = "^[a-z0-9-]{3,30}$") private String slug;
        @NotBlank @Email private String email;
        private String phone;
        @NotBlank private String plan; // STARTER | PROFESSIONAL | ENTERPRISE
    }

    @Data
    public static class TenantOnboardingResponse {
        private final String tenantSlug;
        private final String schemaName;
        private final String message = "Workspace created successfully. Welcome to KerjaPro!";
    }
}
