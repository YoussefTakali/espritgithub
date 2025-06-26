// TaskRequestDTO.java
package com.example.esprit.dto;

import com.example.esprit.model.AssignmentScope;
import com.example.esprit.model.TaskStatus;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskRequestDTO {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String createdBy;
    private Long projectId;
    
    // Assignment info
    private AssignmentScope scope;
    
    private List<Long> groupIds;
    private List<Long> classIds;
    private List<String> studentIds;
    private TaskStatus status;
    private Boolean isGraded;
    private Boolean isVisible;
}
