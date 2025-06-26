package com.example.esprit.controller;

import com.example.esprit.dto.ProjectDTO;
import com.example.esprit.model.Project;
import com.example.esprit.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProjectById(id);
        return ResponseEntity.noContent().build();  // 204 No Content on success
    }
    // Add a new project
    @PostMapping("/add")
    public ResponseEntity<Project> addProject(@RequestBody Project project) {
        Project savedProject = projectService.addProject(project);
        return ResponseEntity.ok(savedProject);
    }

    // Get projects by teacher ID - updated to return DTOs
    @GetMapping("/by-teacher/{teacherId}")
    public ResponseEntity<List<ProjectDTO>> getProjectsByTeacher(@PathVariable String teacherId) {
        List<ProjectDTO> projects = projectService.getProjectsByTeacher(teacherId);
        return ResponseEntity.ok(projects);
    }
    @GetMapping("/by-member/{memberId}")
    public ResponseEntity<List<Project>> getProjectsByMemberId(@PathVariable String memberId) {
        List<Project> projects = projectService.getProjectsByMemberId(memberId);
        return ResponseEntity.ok(projects);
    }
    // Get projects by class ID - updated to return DTOs
    @GetMapping("/by-class/{classId}")
    public ResponseEntity<List<ProjectDTO>> getProjectsByClass(@PathVariable Long classId) {
        List<ProjectDTO> projects = projectService.getProjectsByClass(classId);
        return ResponseEntity.ok(projects);
    }

    // Additional endpoint to get project by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
     @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project updatedProject) {
        Optional<Project> updated = projectService.updateProject(id, updatedProject);
        return updated.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }
}