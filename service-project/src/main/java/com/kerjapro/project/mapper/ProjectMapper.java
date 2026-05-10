package com.kerjapro.project.mapper;

import com.kerjapro.project.dto.request.CreateProjectRequest;
import com.kerjapro.project.dto.request.CreateWorkPackageRequest;
import com.kerjapro.project.dto.response.ProjectDto;
import com.kerjapro.project.dto.response.WorkPackageDto;
import com.kerjapro.project.entity.Project;
import com.kerjapro.project.entity.WorkPackage;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    ProjectDto toDto(Project entity);

    @Mapping(target = "mainContractorId", ignore = true)
    @Mapping(target = "status",           ignore = true)
    @Mapping(target = "workPackages",     ignore = true)
    Project toEntity(CreateProjectRequest request);

    @Mapping(target = "projectId", source = "project.id")
    WorkPackageDto toDto(WorkPackage entity);

    @Mapping(target = "project",      ignore = true)
    @Mapping(target = "status",       ignore = true)
    @Mapping(target = "assignedSubId", ignore = true)
    WorkPackage toEntity(CreateWorkPackageRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "mainContractorId", ignore = true)
    @Mapping(target = "workPackages",     ignore = true)
    void updateProject(CreateProjectRequest request, @MappingTarget Project project);
}
