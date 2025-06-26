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
    public void deleteProjectById(Long id) {
        projectRepository.deleteById(id);
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
    public List<Project> getProjectsByMemberId(String memberId) {
        return projectRepository.findProjectsByMemberId(memberId);
    }
    @Transactional(readOnly = true)
    public Optional<ProjectDTO> getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(ProjectMapper::toDto);
    }
 @Transactional
    public Optional<Project> updateProject(Long id, Project updatedProject) {
        return projectRepository.findById(id)
                .map(existingProject -> {
                    existingProject.setTitle(updatedProject.getTitle());
                    existingProject.setDescription(updatedProject.getDescription());
                    existingProject.setDeadline(updatedProject.getDeadline());
                    existingProject.setCreatedBy(updatedProject.getCreatedBy());
                    existingProject.setAssociatedClasses(updatedProject.getAssociatedClasses());
                    existingProject.setMembers(updatedProject.getMembers());
                    return projectRepository.save(existingProject);
                });
    }
}