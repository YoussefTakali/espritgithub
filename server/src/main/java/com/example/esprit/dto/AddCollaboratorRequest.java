package com.example.esprit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddCollaboratorRequest {
    private String owner;
    private String repo;
    private String username;
    private String permission; // e.g. "push", "pull", "admin"
}
