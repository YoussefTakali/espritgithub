package com.example.esprit.service;

import com.example.esprit.dto.ProjectDTO;
import com.example.esprit.model.Project;
import com.example.esprit.repository.ProjectRepository;
import com.example.esprit.util.ProjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Project addProject(Project project) {
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByTeacher(String teacherId) {
        return ProjectMapper.toDtoList(projectRepository.findByCreatedBy(teacherId));
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByClass(Long classId) {
        return ProjectMapper.toDtoList(projectRepository.findByAssociatedClassesId(classId));
    }

    @Transactional(readOnly = true)
    public Optional<ProjectDTO> getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(ProjectMapper::toDto);
    }

}