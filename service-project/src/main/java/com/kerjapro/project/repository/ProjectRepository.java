package com.kerjapro.project.repository;

import com.kerjapro.project.entity.Project;
import com.kerjapro.project.entity.Project.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Page<Project> findByMainContractorIdAndDeletedAtIsNull(String contractorId, Pageable pageable);

    Page<Project> findByMainContractorIdAndStatusAndDeletedAtIsNull(
            String contractorId, ProjectStatus status, Pageable pageable);

    Optional<Project> findByIdAndDeletedAtIsNull(UUID id);

    Optional<Project> findByIdAndMainContractorIdAndDeletedAtIsNull(UUID id, String contractorId);
}
