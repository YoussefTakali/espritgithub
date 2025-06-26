package com.example.esprit.dto;

import com.example.esprit.model.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {
    
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime dueDate;
    private ProjectStatus status;
    private String createdBy;
    private Set<String> collaborators;
    private List<ClassDTO> associatedClasses;
    private List<GroupDTO> groups; 
}