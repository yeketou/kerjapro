package com.kerjapro.contractor.dto.request;

import com.kerjapro.contractor.entity.TradeSpecialization.TradeCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateTradeRequest {

    @NotNull(message = "Trade category is required")
    private TradeCategory tradeCategory;

    @Min(0) @Max(60)
    private Integer yearsExperience;

    @Size(max = 500)
    private String description;
}
