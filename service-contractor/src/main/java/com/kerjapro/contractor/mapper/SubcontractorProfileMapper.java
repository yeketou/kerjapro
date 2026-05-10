package com.kerjapro.contractor.mapper;

import com.kerjapro.contractor.dto.request.CreateProfileRequest;
import com.kerjapro.contractor.dto.request.UpdateProfileRequest;
import com.kerjapro.contractor.dto.response.SubcontractorProfileDto;
import com.kerjapro.contractor.entity.SubcontractorProfile;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = { TradeSpecializationMapper.class, BrandCertificationMapper.class, PortfolioMapper.class },
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SubcontractorProfileMapper {

    SubcontractorProfileDto toDto(SubcontractorProfile entity);

    @Mapping(target = "userId",             ignore = true)
    @Mapping(target = "subscriptionTier",   ignore = true)
    @Mapping(target = "verified",           ignore = true)
    @Mapping(target = "available",          ignore = true)
    @Mapping(target = "averageRating",      ignore = true)
    @Mapping(target = "totalReviews",       ignore = true)
    @Mapping(target = "totalCompletedJobs", ignore = true)
    SubcontractorProfile toEntity(CreateProfileRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId",             ignore = true)
    @Mapping(target = "email",              ignore = true)
    @Mapping(target = "subscriptionTier",   ignore = true)
    @Mapping(target = "verified",           ignore = true)
    @Mapping(target = "averageRating",      ignore = true)
    @Mapping(target = "totalReviews",       ignore = true)
    @Mapping(target = "totalCompletedJobs", ignore = true)
    void updateEntity(UpdateProfileRequest request, @MappingTarget SubcontractorProfile entity);
}
