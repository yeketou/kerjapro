package com.kerjapro.contractor.controller;

import com.kerjapro.contractor.dto.request.SearchRequest;
import com.kerjapro.contractor.dto.response.PagedResponse;
import com.kerjapro.contractor.dto.response.SubcontractorProfileDto;
import com.kerjapro.contractor.entity.TradeSpecialization.TradeCategory;
import com.kerjapro.contractor.service.SubcontractorProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/contractors/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Search and discover subcontractors")
public class SearchController {

    private final SubcontractorProfileService service;

    @GetMapping
    @Operation(summary = "Search subcontractors with filters (public endpoint)")
    public ResponseEntity<PagedResponse<SubcontractorProfileDto>> search(
            @RequestParam(required = false) TradeCategory tradeCategory,
            @RequestParam(required = false) String brandName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "TIER") String sortBy) {

        SearchRequest req = new SearchRequest();
        req.setTradeCategory(tradeCategory);
        req.setBrandName(brandName);
        req.setCity(city);
        req.setState(state);
        req.setMinRating(minRating);
        req.setVerified(verified);
        req.setPage(page);
        req.setSize(Math.min(size, 50)); // cap at 50
        req.setSortBy(sortBy);

        return ResponseEntity.ok(service.search(req));
    }
}
