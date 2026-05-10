package com.kerjapro.contractor.dto.response;

import com.kerjapro.contractor.entity.TradeSpecialization.TradeCategory;
import lombok.Data;
import java.util.UUID;

@Data
public class TradeSpecializationDto {
    private UUID id;
    private TradeCategory tradeCategory;
    private Integer yearsExperience;
    private String description;
}
