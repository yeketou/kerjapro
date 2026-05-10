package com.kerjapro.contractor.mapper;

import com.kerjapro.contractor.dto.request.CreateProfileRequest;
import com.kerjapro.contractor.dto.request.UpdateProfileRequest;
import com.kerjapro.contractor.dto.response.SubcontractorProfileDto;
import com.kerjapro.contractor.entity.SubcontractorProfile;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = { TradeSpecializationMapper.class, BrandCertificationMapper.class, PortfolioMapper.class },
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SubcontractorProfileMapper {

    SubcontractorProfileDto toDto(SubcontractorProfile entity);

    @Mapping(target = "id",                 ignore = true)
    @Mapping(target = "userId",             ignore = true)
    @Mapping(target = "profilePhotoUrl",    ignore = true)
    @Mapping(target = "subscriptionTier",   ignore = true)
    @Mapping(target = "verified",           ignore = true)
    @Mapping(target = "available",          ignore = true)
    @Mapping(target = "averageRating",      ignore = true)
    @Mapping(target = "totalReviews",       ignore = true)
    @Mapping(target = "totalCompletedJobs", ignore = true)
    @Mapping(target = "tradeSpecializations", ignore = true)
    @Mapping(target = "brandCertifications",  ignore = true)
    @Mapping(target = "portfolio",            ignore = true)
    @Mapping(target = "createdAt",  ignore = true)
    @Mapping(target = "createdBy",  ignore = true)
    @Mapping(target = "updatedAt",  ignore = true)
    @Mapping(target = "updatedBy",  ignore = true)
    @Mapping(target = "deletedAt",  ignore = true)
    @Mapping(target = "version",    ignore = true)
    SubcontractorProfile toEntity(CreateProfileRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",                 ignore = true)
    @Mapping(target = "userId",             ignore = true)
    @Mapping(target = "email",              ignore = true)
    @Mapping(target = "profilePhotoUrl",    ignore = true)
    @Mapping(target = "subscriptionTier",   ignore = true)
    @Mapping(target = "verified",           ignore = true)
    @Mapping(target = "averageRating",      ignore = true)
    @Mapping(target = "totalReviews",       ignore = true)
    @Mapping(target = "totalCompletedJobs", ignore = true)
    @Mapping(target = "tradeSpecializations", ignore = true)
    @Mapping(target = "brandCertifications",  ignore = true)
    @Mapping(target = "portfolio",            ignore = true)
    @Mapping(target = "createdAt",  ignore = true)
    @Mapping(target = "createdBy",  ignore = true)
    @Mapping(target = "updatedAt",  ignore = true)
    @Mapping(target = "updatedBy",  ignore = true)
    @Mapping(target = "deletedAt",  ignore = true)
    @Mapping(target = "version",    ignore = true)
    void updateEntity(UpdateProfileRequest request, @MappingTarget SubcontractorProfile entity);
}
