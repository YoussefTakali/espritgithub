package com.example.esprit.dto;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupResponseDTO {
    private Long id;
    private String name;
    private String projectName;
    private String className;
    private Set<Long> memberIds;
    private Long projectId;
    private Long classId;
}