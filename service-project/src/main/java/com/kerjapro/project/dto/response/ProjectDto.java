package com.kerjapro.project.dto.response;

import com.kerjapro.project.entity.Project.ProjectStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class ProjectDto {
    private UUID id;
    private String mainContractorId;
    private String title;
    private String description;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
    private List<WorkPackageDto> workPackages;
}
