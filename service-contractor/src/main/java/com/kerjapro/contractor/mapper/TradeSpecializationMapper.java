package com.kerjapro.contractor.mapper;

import com.kerjapro.contractor.dto.request.CreateTradeRequest;
import com.kerjapro.contractor.dto.response.TradeSpecializationDto;
import com.kerjapro.contractor.entity.TradeSpecialization;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TradeSpecializationMapper {

    TradeSpecializationDto toDto(TradeSpecialization entity);

    @Mapping(target = "profile", ignore = true)
    TradeSpecialization toEntity(CreateTradeRequest request);
}
