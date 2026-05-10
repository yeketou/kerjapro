package com.kerjapro.project.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateProjectRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @Size(max = 2000)
    private String description;

    @Size(max = 255)
    private String location;

    private LocalDate startDate;
    private LocalDate endDate;
}
