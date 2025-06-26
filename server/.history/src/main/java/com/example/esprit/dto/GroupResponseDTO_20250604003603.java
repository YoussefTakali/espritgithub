package com.example.esprit.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class GroupResponseDTO {
    private Long id;
    private String name;
    private String projectName;
    private String className;
    private List<Long> memberIds;
    // add these two fields:
    private Long projectId;
    private Long classId;
}
