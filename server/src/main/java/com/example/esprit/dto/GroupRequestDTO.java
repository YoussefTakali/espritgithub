package com.example.esprit.dto;

import lombok.Data;

import java.util.Set;

@Data
public class GroupRequestDTO {
    private String name;
    private Long projectId;
    private Long classId;
    private Set<String> memberIds;
}
