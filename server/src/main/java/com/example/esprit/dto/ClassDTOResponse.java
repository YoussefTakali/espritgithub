package com.example.esprit.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ClassDTOResponse {
    private Long id;
    private String name;
    private String description;
    private String academicYear;
    private String subject;
    private List<ProjectDTOResponse> projects;
}