package com.kerjapro.project.controller;

import com.kerjapro.project.dto.request.CreateProjectRequest;
import com.kerjapro.project.dto.request.CreateWorkPackageRequest;
import com.kerjapro.project.dto.response.ProjectDto;
import com.kerjapro.project.dto.response.WorkPackageDto;
import com.kerjapro.project.entity.Project.ProjectStatus;
import com.kerjapro.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project and work package management (tenant-scoped)")
public class ProjectController {

    private final ProjectService service;

    // ── Projects ─────────────────────────────────

    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<ProjectDto> create(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createProject(userId, request));
    }

    @GetMapping
    @Operation(summary = "List my projects")
    public ResponseEntity<Page<ProjectDto>> list(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.getMyProjects(userId, status, PageRequest.of(page, size)));
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Get project details")
    public ResponseEntity<ProjectDto> get(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getProject(userId, projectId));
    }

    @PatchMapping("/{projectId}")
    @Operation(summary = "Update project details")
    public ResponseEntity<ProjectDto> update(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateProjectRequest request) {
        return ResponseEntity.ok(service.updateProject(userId, projectId, request));
    }

    @PostMapping("/{projectId}/activate")
    @Operation(summary = "Activate project (DRAFT → ACTIVE)")
    public ResponseEntity<ProjectDto> activate(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.activateProject(userId, projectId));
    }

    @PostMapping("/{projectId}/complete")
    @Operation(summary = "Mark project as completed")
    public ResponseEntity<ProjectDto> complete(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.completeProject(userId, projectId));
    }

    @PostMapping("/{projectId}/cancel")
    @Operation(summary = "Cancel a project")
    public ResponseEntity<ProjectDto> cancel(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.cancelProject(userId, projectId));
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a project (soft delete)")
    public void delete(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID projectId) {
        service.deleteProject(userId, projectId);
    }

    // ── Work Packages ─────────────────────────────

    @PostMapping("/{projectId}/packages")
    @Operation(summary = "Add a work package to a project")
    public ResponseEntity<WorkPackageDto> addPackage(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateWorkPackageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addWorkPackage(userId, projectId, request));
    }

    @GetMapping("/{projectId}/packages")
    @Operation(summary = "List all work packages in a project")
    public ResponseEntity<List<WorkPackageDto>> listPackages(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getWorkPackages(userId, projectId));
    }

    @PatchMapping("/{projectId}/packages/{packageId}")
    @Operation(summary = "Update a work package")
    public ResponseEntity<WorkPackageDto> updatePackage(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID projectId,
            @PathVariable UUID packageId,
            @Valid @RequestBody CreateWorkPackageRequest request) {
        return ResponseEntity.ok(service.updateWorkPackage(userId, projectId, packageId, request));
    }

    @PostMapping("/{projectId}/packages/{packageId}/assign/{subProfileId}")
    @Operation(summary = "Assign a subcontractor to a work package")
    public ResponseEntity<WorkPackageDto> assign(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID projectId,
            @PathVariable UUID packageId,
            @PathVariable UUID subProfileId) {
        return ResponseEntity.ok(service.assignSubcontractor(userId, projectId, packageId, subProfileId));
    }

    @DeleteMapping("/{projectId}/packages/{packageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a work package")
    public void deletePackage(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID projectId,
            @PathVariable UUID packageId) {
        service.deleteWorkPackage(userId, projectId, packageId);
    }
}
