package com.example.esprit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "repository_descriptions", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"owner", "repository_name"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner", nullable = false, length = 100)
    private String owner;

    @Column(name = "repository_name", nullable = false, length = 100)
    private String repositoryName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    // Constructor with required fields
    public RepositoryDescription(String owner, String repositoryName, String description, String username) {
        this.owner = owner;
        this.repositoryName = repositoryName;
        this.description = description;
        this.createdBy = username;
        this.updatedBy = username;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Lifecycle methods
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
