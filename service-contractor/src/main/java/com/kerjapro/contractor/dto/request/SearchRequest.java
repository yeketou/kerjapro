package com.kerjapro.contractor.dto.request;

import com.kerjapro.contractor.entity.TradeSpecialization.TradeCategory;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchRequest {

    private TradeCategory tradeCategory;
    private String brandName;
    private String city;
    private String state;
    private BigDecimal minRating;
    private Boolean verified;
    private Boolean available = true;

    // Pagination
    private int page = 0;
    private int size = 20;

    // Sorting: RATING, REVIEWS, JOBS, TIER
    private String sortBy = "TIER";
}
