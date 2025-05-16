package com.example.esprit.controller;

import com.example.esprit.model.Project;
import com.example.esprit.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // Add a new project
    @PostMapping("/add")
    public ResponseEntity<Project> addProject(@RequestBody Project project) {
        Project savedProject = projectService.addProject(project);
        return ResponseEntity.ok(savedProject);
    }

    // Get projects by teacher ID
    @GetMapping("/by-teacher/{teacherId}")
    public ResponseEntity<List<Project>> getProjectsByTeacher(@PathVariable String teacherId) {
        List<Project> projects = projectService.getProjectsByTeacher(teacherId);
        return ResponseEntity.ok(projects);
    }

    // Get projects by class ID
    @GetMapping("/by-class/{classId}")
    public ResponseEntity<List<Project>> getProjectsByClass(@PathVariable Long classId) {
        List<Project> projects = projectService.getProjectsByClass(classId);
        return ResponseEntity.ok(projects);
    }
}
