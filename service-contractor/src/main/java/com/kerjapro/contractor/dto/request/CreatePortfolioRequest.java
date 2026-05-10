package com.kerjapro.contractor.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreatePortfolioRequest {

    @NotBlank(message = "Project title is required")
    @Size(max = 255)
    private String projectTitle;

    @Size(max = 2000)
    private String projectDescription;

    @Size(max = 255)
    private String location;

    private LocalDate completedDate;

    @Size(max = 100)
    private String brandUsed;

    @Size(max = 10, message = "Maximum 10 photos per portfolio item")
    private List<String> photoUrls;
}
