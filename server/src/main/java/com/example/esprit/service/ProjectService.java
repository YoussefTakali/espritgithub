package com.example.esprit.service;

import com.example.esprit.model.Project;
import com.example.esprit.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    // Save a new project
    public Project addProject(Project project) {
        return projectRepository.save(project);
    }

    // Find projects by teacher ID
    public List<Project> getProjectsByTeacher(String teacherId) {
        return projectRepository.findByCreatedBy(teacherId);
    }

    // Find projects by class ID
    public List<Project> getProjectsByClass(Long classId) {
        return projectRepository.findByAssociatedClassId(classId);
    }

    // Optional: find by project ID
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }
}
