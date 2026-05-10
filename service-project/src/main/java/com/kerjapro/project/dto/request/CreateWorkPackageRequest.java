package com.kerjapro.project.dto.request;

import com.kerjapro.project.entity.WorkPackage.TradeCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateWorkPackageRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @NotNull(message = "Trade category is required")
    private TradeCategory tradeCategory;

    @Size(max = 2000)
    private String description;

    @Size(max = 100)
    private String requiredBrand;

    @DecimalMin("0.00")
    private BigDecimal budgetMin;

    @DecimalMin("0.00")
    private BigDecimal budgetMax;

    private LocalDate startDate;
    private LocalDate endDate;
}
