package com.example.esprit.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.example.esprit.model.ProjectStatus;

import lombok.Builder;
import lombok.Data;
@Data @Builder
public class ProjectDTOResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime dueDate;
    private ProjectStatus status;
    private String createdBy;
    private Set<String> collaborators;
    private List<GroupDTOResponse> groups;
}
