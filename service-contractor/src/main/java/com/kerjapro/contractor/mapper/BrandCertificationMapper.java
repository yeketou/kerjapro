package com.kerjapro.contractor.mapper;

import com.kerjapro.contractor.dto.request.CreateCertificationRequest;
import com.kerjapro.contractor.dto.response.BrandCertificationDto;
import com.kerjapro.contractor.entity.BrandCertification;
import org.mapstruct.*;

import java.time.LocalDate;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BrandCertificationMapper {

    @Mapping(target = "expired", expression = "java(isExpired(entity))")
    BrandCertificationDto toDto(BrandCertification entity);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "profile",   ignore = true)
    @Mapping(target = "verified",  ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "version",   ignore = true)
    BrandCertification toEntity(CreateCertificationRequest request);

    default boolean isExpired(BrandCertification entity) {
        return entity.getExpiryDate() != null && entity.getExpiryDate().isBefore(LocalDate.now());
    }
}
