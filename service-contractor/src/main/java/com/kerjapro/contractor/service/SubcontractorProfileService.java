package com.kerjapro.contractor.service;

import com.kerjapro.common.exception.BusinessException;
import com.kerjapro.common.exception.ResourceNotFoundException;
import com.kerjapro.contractor.dto.request.*;
import com.kerjapro.contractor.dto.response.*;
import com.kerjapro.contractor.entity.*;
import com.kerjapro.contractor.mapper.*;
import com.kerjapro.contractor.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubcontractorProfileService {

    private final SubcontractorProfileRepository profileRepo;
    private final TradeSpecializationRepository  tradeRepo;
    private final BrandCertificationRepository   certRepo;
    private final PortfolioItemRepository        portfolioRepo;
    private final SubcontractorProfileMapper     profileMapper;
    private final TradeSpecializationMapper      tradeMapper;
    private final BrandCertificationMapper       certMapper;
    private final PortfolioMapper                portfolioMapper;

    // ─────────────────────────────────────────────
    // Profile CRUD
    // ─────────────────────────────────────────────

    @Transactional
    public SubcontractorProfileDto createProfile(String userId, CreateProfileRequest request) {
        if (profileRepo.existsByUserIdAndDeletedAtIsNull(userId)) {
            throw new BusinessException(
                "Profile already exists for this user",
                HttpStatus.CONFLICT, "PROFILE_EXISTS");
        }
        SubcontractorProfile profile = profileMapper.toEntity(request);
        profile.setUserId(userId);
        SubcontractorProfile saved = profileRepo.save(profile);
        log.info("Created subcontractor profile for userId={}, profileId={}", userId, saved.getId());
        return profileMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public SubcontractorProfileDto getProfileById(UUID profileId) {
        return profileMapper.toDto(findActiveById(profileId));
    }

    @Transactional(readOnly = true)
    public SubcontractorProfileDto getMyProfile(String userId) {
        SubcontractorProfile profile = profileRepo.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SubcontractorProfile", userId));
        return profileMapper.toDto(profile);
    }

    @Transactional
    public SubcontractorProfileDto updateProfile(String userId, UpdateProfileRequest request) {
        SubcontractorProfile profile = profileRepo.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SubcontractorProfile", userId));
        profileMapper.updateEntity(request, profile);
        return profileMapper.toDto(profileRepo.save(profile));
    }

    @Transactional
    public void deleteProfile(String userId) {
        SubcontractorProfile profile = profileRepo.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SubcontractorProfile", userId));
        profile.softDelete();
        profileRepo.save(profile);
        log.info("Soft-deleted profile for userId={}", userId);
    }

    @Transactional
    public SubcontractorProfileDto updatePhoto(String userId, String photoUrl) {
        SubcontractorProfile profile = profileRepo.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SubcontractorProfile", userId));
        profile.setProfilePhotoUrl(photoUrl);
        return profileMapper.toDto(profileRepo.save(profile));
    }

    // ─────────────────────────────────────────────
    // Trade Specializations
    // ─────────────────────────────────────────────

    @Transactional
    public TradeSpecializationDto addTrade(String userId, CreateTradeRequest request) {
        SubcontractorProfile profile = profileRepo.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SubcontractorProfile", userId));
        TradeSpecialization trade = tradeMapper.toEntity(request);
        trade.setProfile(profile);
        return tradeMapper.toDto(tradeRepo.save(trade));
    }

    @Transactional
    public void removeTrade(String userId, UUID tradeId) {
        SubcontractorProfile profile = profileRepo.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SubcontractorProfile", userId));
        TradeSpecialization trade = tradeRepo.findById(tradeId)
                .filter(t -> t.getProfile().getId().equals(profile.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("TradeSpecialization", tradeId));
        trade.softDelete();
        tradeRepo.save(trade);
    }

    // ─────────────────────────────────────────────
    // Brand Certifications
    // ─────────────────────────────────────────────

    @Transactional
    public BrandCertificationDto addCertification(String userId, CreateCertificationRequest request) {
        SubcontractorProfile profile = profileRepo.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SubcontractorProfile", userId));
        BrandCertification cert = certMapper.toEntity(request);
        cert.setProfile(profile);
        return certMapper.toDto(certRepo.save(cert));
    }

    @Transactional
    public void removeCertification(String userId, UUID certId) {
        SubcontractorProfile profile = profileRepo.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SubcontractorProfile", userId));
        BrandCertification cert = certRepo.findByIdAndProfileIdAndDeletedAtIsNull(certId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("BrandCertification", certId));
        cert.softDelete();
        certRepo.save(cert);
    }

    // ─────────────────────────────────────────────
    // Portfolio
    // ─────────────────────────────────────────────

    @Transactional
    public PortfolioItemDto addPortfolioItem(String userId, CreatePortfolioRequest request) {
        SubcontractorProfile profile = profileRepo.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SubcontractorProfile", userId));
        int count = portfolioRepo.countByProfileIdAndDeletedAtIsNull(profile.getId());
        if (count >= 20) {
            throw new BusinessException("Maximum 20 portfolio items allowed", HttpStatus.BAD_REQUEST, "PORTFOLIO_LIMIT");
        }
        PortfolioItem item = portfolioMapper.toEntity(request);
        item.setProfile(profile);
        return portfolioMapper.toDto(portfolioRepo.save(item));
    }

    @Transactional
    public void removePortfolioItem(String userId, UUID itemId) {
        SubcontractorProfile profile = profileRepo.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SubcontractorProfile", userId));
        PortfolioItem item = portfolioRepo.findByIdAndProfileIdAndDeletedAtIsNull(itemId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("PortfolioItem", itemId));
        item.softDelete();
        portfolioRepo.save(item);
    }

    // ─────────────────────────────────────────────
    // Search
    // ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PagedResponse<SubcontractorProfileDto> search(SearchRequest req) {
        Pageable pageable = PageRequest.of(req.getPage(), req.getSize());

        Specification<SubcontractorProfile> spec = Specification
                .where(notDeleted())
                .and(isAvailable(req.getAvailable()))
                .and(byCity(req.getCity()))
                .and(byState(req.getState()))
                .and(minRating(req.getMinRating()))
                .and(isVerified(req.getVerified()))
                .and(byTrade(req.getTradeCategory()))
                .and(byBrand(req.getBrandName()));

        Page<SubcontractorProfile> page = profileRepo.findAll(spec, pageable);
        Page<SubcontractorProfileDto> dtoPage = page.map(profileMapper::toDto);
        return PagedResponse.of(dtoPage);
    }

    // ─────────────────────────────────────────────
    // Specifications
    // ─────────────────────────────────────────────

    private Specification<SubcontractorProfile> notDeleted() {
        return (r, q, cb) -> cb.isNull(r.get("deletedAt"));
    }

    private Specification<SubcontractorProfile> isAvailable(Boolean available) {
        if (available == null) return null;
        return (r, q, cb) -> cb.equal(r.get("available"), available);
    }

    private Specification<SubcontractorProfile> byCity(String city) {
        if (city == null || city.isBlank()) return null;
        return (r, q, cb) -> cb.equal(cb.lower(r.get("city")), city.toLowerCase());
    }

    private Specification<SubcontractorProfile> byState(String state) {
        if (state == null || state.isBlank()) return null;
        return (r, q, cb) -> cb.equal(cb.lower(r.get("state")), state.toLowerCase());
    }

    private Specification<SubcontractorProfile> minRating(BigDecimal min) {
        if (min == null) return null;
        return (r, q, cb) -> cb.greaterThanOrEqualTo(r.get("averageRating"), min);
    }

    private Specification<SubcontractorProfile> isVerified(Boolean verified) {
        if (verified == null) return null;
        return (r, q, cb) -> cb.equal(r.get("verified"), verified);
    }

    private Specification<SubcontractorProfile> byTrade(TradeSpecialization.TradeCategory category) {
        if (category == null) return null;
        return (r, q, cb) -> {
            var join = r.join("tradeSpecializations");
            return cb.and(
                cb.equal(join.get("tradeCategory"), category),
                cb.isNull(join.get("deletedAt"))
            );
        };
    }

    private Specification<SubcontractorProfile> byBrand(String brandName) {
        if (brandName == null || brandName.isBlank()) return null;
        return (r, q, cb) -> {
            var join = r.join("brandCertifications");
            return cb.and(
                cb.equal(cb.lower(join.get("brandName")), brandName.toLowerCase()),
                cb.isNull(join.get("deletedAt"))
            );
        };
    }

    // ─────────────────────────────────────────────
    // Internal helpers
    // ─────────────────────────────────────────────

    private SubcontractorProfile findActiveById(UUID id) {
        return profileRepo.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("SubcontractorProfile", id));
    }
}
