package com.example.esprit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Commit {
    @Id
    private String sha; // Commit SHA as unique ID

    private String branch;
    private String message;
    private String authorName;
    private String authorLogin;
    private String authorAvatarUrl;
    private String date;
    private String repoName;

    @ManyToOne
    @JoinColumn(name = "repository_id")
    private RepositoryEntity repository;
}
