package com.example.esprit.dto;

import java.time.LocalDateTime;
import com.example.esprit.model.TaskStatus;

public class TaskSummaryDTO {
    private Long id;
    private String title;
    private LocalDateTime dueDate;
    private TaskStatus status;
    private Long projectId;  // <-- Add projectId here

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public TaskSummaryDTO(Long id, String title, LocalDateTime dueDate, TaskStatus status, Long projectId) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.status = status;
        this.projectId = projectId;
    }

}
