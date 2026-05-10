package com.kerjapro.contractor.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class PortfolioItemDto {
    private UUID id;
    private String projectTitle;
    private String projectDescription;
    private String location;
    private LocalDate completedDate;
    private String brandUsed;
    private List<String> photoUrls;
}
