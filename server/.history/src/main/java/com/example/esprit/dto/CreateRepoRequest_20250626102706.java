package com.example.esprit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRepoRequest {
    private String name;
    private String description;
    private String ownerName;
    private boolean privateRepo;
    private boolean autoInit;
    private String gitignoreTemplate;
    private Long classId;
}
