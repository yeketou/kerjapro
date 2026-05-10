package com.kerjapro.contractor.mapper;

import com.kerjapro.contractor.dto.request.CreatePortfolioRequest;
import com.kerjapro.contractor.dto.response.PortfolioItemDto;
import com.kerjapro.contractor.entity.PortfolioItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PortfolioMapper {

    PortfolioItemDto toDto(PortfolioItem entity);

    @Mapping(target = "profile", ignore = true)
    PortfolioItem toEntity(CreatePortfolioRequest request);
}
