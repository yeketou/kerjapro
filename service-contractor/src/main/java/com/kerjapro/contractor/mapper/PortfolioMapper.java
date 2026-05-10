package com.kerjapro.contractor.mapper;

import com.kerjapro.contractor.dto.request.CreatePortfolioRequest;
import com.kerjapro.contractor.dto.response.PortfolioItemDto;
import com.kerjapro.contractor.entity.PortfolioItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PortfolioMapper {

    PortfolioItemDto toDto(PortfolioItem entity);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "profile",   ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "version",   ignore = true)
    PortfolioItem toEntity(CreatePortfolioRequest request);
}
