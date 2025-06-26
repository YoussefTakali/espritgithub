package com.example.esprit.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.example.esprit.model.TaskStatus;

public class TaskDTO {
    private Long id;
    private Long projectId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String createdBy;
    private TaskStatus status;
    private LocalDateTime createdDate;
    private Set<TaskAssignmentDTO> assignments;

    private boolean isGraded;
    private boolean isVisible;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Set<TaskAssignmentDTO> getAssignments() {
        return assignments;
    }

    public void setAssignments(Set<TaskAssignmentDTO> assignments) {
        this.assignments = assignments;
    }

    public boolean setisGraded() {
        return isGraded;
    }

    public void setisGraded(boolean graded) {
        isGraded = graded;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
