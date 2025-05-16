package com.example.esprit.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;
    
    @Column(name = "created_by", nullable = false)
    private String createdBy; // Keycloak user ID of the teacher
    
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Class associatedClass;
    
    // Relationships
    @OneToMany(mappedBy = "project")
        @JsonIgnore
    private Set<ProjectStudentEnrollment> studentEnrollments;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Task> tasks;
}
enum ProjectStatus {
    ACTIVE,
    COMPLETED,
    ARCHIVED
}