package com.kerjapro.project.service;

import com.kerjapro.common.exception.BusinessException;
import com.kerjapro.common.exception.ResourceNotFoundException;
import com.kerjapro.common.tenant.TenantContext;
import com.kerjapro.project.dto.request.CreateProjectRequest;
import com.kerjapro.project.dto.request.CreateWorkPackageRequest;
import com.kerjapro.project.dto.response.ProjectDto;
import com.kerjapro.project.dto.response.WorkPackageDto;
import com.kerjapro.project.entity.Project;
import com.kerjapro.project.entity.Project.ProjectStatus;
import com.kerjapro.project.entity.WorkPackage;
import com.kerjapro.project.entity.WorkPackage.PackageStatus;
import com.kerjapro.project.mapper.ProjectMapper;
import com.kerjapro.project.repository.ProjectRepository;
import com.kerjapro.project.repository.WorkPackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository    projectRepo;
    private final WorkPackageRepository packageRepo;
    private final ProjectMapper         mapper;

    // ─────────────────────────────────────────────
    // Projects
    // ─────────────────────────────────────────────

    @Transactional
    public ProjectDto createProject(String userId, CreateProjectRequest request) {
        requireTenantContext();
        Project project = mapper.toEntity(request);
        project.setMainContractorId(userId);
        project.setStatus(ProjectStatus.DRAFT);
        Project saved = projectRepo.save(project);
        log.info("Project created: id={}, tenant={}", saved.getId(), TenantContext.getTenantSlug());
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<ProjectDto> getMyProjects(String userId, ProjectStatus status, Pageable pageable) {
        requireTenantContext();
        Page<Project> page = (status != null)
                ? projectRepo.findByMainContractorIdAndStatusAndDeletedAtIsNull(userId, status, pageable)
                : projectRepo.findByMainContractorIdAndDeletedAtIsNull(userId, pageable);
        return page.map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProjectDto getProject(String userId, UUID projectId) {
        return mapper.toDto(findOwned(userId, projectId));
    }

    @Transactional
    public ProjectDto updateProject(String userId, UUID projectId, CreateProjectRequest request) {
        Project project = findOwned(userId, projectId);
        mapper.updateProject(request, project);
        return mapper.toDto(projectRepo.save(project));
    }

    @Transactional
    public ProjectDto activateProject(String userId, UUID projectId) {
        return changeStatus(userId, projectId, ProjectStatus.ACTIVE);
    }

    @Transactional
    public ProjectDto completeProject(String userId, UUID projectId) {
        return changeStatus(userId, projectId, ProjectStatus.COMPLETED);
    }

    @Transactional
    public ProjectDto cancelProject(String userId, UUID projectId) {
        return changeStatus(userId, projectId, ProjectStatus.CANCELLED);
    }

    @Transactional
    public void deleteProject(String userId, UUID projectId) {
        Project project = findOwned(userId, projectId);
        if (project.getStatus() == ProjectStatus.ACTIVE) {
            throw new BusinessException(
                "Cannot delete an active project. Cancel it first.",
                HttpStatus.CONFLICT, "PROJECT_ACTIVE");
        }
        project.softDelete();
        projectRepo.save(project);
    }

    // ─────────────────────────────────────────────
    // Work Packages
    // ─────────────────────────────────────────────

    @Transactional
    public WorkPackageDto addWorkPackage(String userId, UUID projectId,
                                         CreateWorkPackageRequest request) {
        Project project = findOwned(userId, projectId);
        WorkPackage pkg = mapper.toEntity(request);
        pkg.setProject(project);
        pkg.setStatus(PackageStatus.OPEN);
        return mapper.toDto(packageRepo.save(pkg));
    }

    @Transactional(readOnly = true)
    public List<WorkPackageDto> getWorkPackages(String userId, UUID projectId) {
        findOwned(userId, projectId); // access check
        return packageRepo.findByProjectIdAndDeletedAtIsNull(projectId)
                .stream().map(mapper::toDto).toList();
    }

    @Transactional
    public WorkPackageDto updateWorkPackage(String userId, UUID projectId,
                                            UUID packageId, CreateWorkPackageRequest request) {
        findOwned(userId, projectId);
        WorkPackage pkg = findPackage(projectId, packageId);
        if (pkg.getStatus() != PackageStatus.OPEN) {
            throw new BusinessException(
                "Only OPEN packages can be edited", HttpStatus.CONFLICT, "PACKAGE_NOT_OPEN");
        }
        if (request.getTitle()         != null) pkg.setTitle(request.getTitle());
        if (request.getDescription()   != null) pkg.setDescription(request.getDescription());
        if (request.getRequiredBrand() != null) pkg.setRequiredBrand(request.getRequiredBrand());
        if (request.getBudgetMin()     != null) pkg.setBudgetMin(request.getBudgetMin());
        if (request.getBudgetMax()     != null) pkg.setBudgetMax(request.getBudgetMax());
        if (request.getStartDate()     != null) pkg.setStartDate(request.getStartDate());
        if (request.getEndDate()       != null) pkg.setEndDate(request.getEndDate());
        return mapper.toDto(packageRepo.save(pkg));
    }

    @Transactional
    public WorkPackageDto assignSubcontractor(String userId, UUID projectId,
                                              UUID packageId, UUID subProfileId) {
        findOwned(userId, projectId);
        WorkPackage pkg = findPackage(projectId, packageId);
        pkg.setAssignedSubId(subProfileId);
        pkg.setStatus(PackageStatus.ASSIGNED);
        return mapper.toDto(packageRepo.save(pkg));
    }

    @Transactional
    public void deleteWorkPackage(String userId, UUID projectId, UUID packageId) {
        findOwned(userId, projectId);
        WorkPackage pkg = findPackage(projectId, packageId);
        pkg.softDelete();
        packageRepo.save(pkg);
    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────

    private void requireTenantContext() {
        if (!TenantContext.hasTenant()) {
            throw new BusinessException(
                "Tenant context is required for project operations",
                HttpStatus.FORBIDDEN, "NO_TENANT_CONTEXT");
        }
    }

    private Project findOwned(String userId, UUID projectId) {
        requireTenantContext();
        return projectRepo.findByIdAndMainContractorIdAndDeletedAtIsNull(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
    }

    private WorkPackage findPackage(UUID projectId, UUID packageId) {
        return packageRepo.findByIdAndProjectIdAndDeletedAtIsNull(packageId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkPackage", packageId));
    }

    private ProjectDto changeStatus(String userId, UUID projectId, ProjectStatus newStatus) {
        Project project = findOwned(userId, projectId);
        project.setStatus(newStatus);
        return mapper.toDto(projectRepo.save(project));
    }
}
