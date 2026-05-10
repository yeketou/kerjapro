package com.kerjapro.contractor.mapper;

import com.kerjapro.contractor.dto.request.CreateTradeRequest;
import com.kerjapro.contractor.dto.response.TradeSpecializationDto;
import com.kerjapro.contractor.entity.TradeSpecialization;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TradeSpecializationMapper {

    TradeSpecializationDto toDto(TradeSpecialization entity);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "profile",   ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "version",   ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    TradeSpecialization toEntity(CreateTradeRequest request);
}
