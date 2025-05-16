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
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    private String description;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "created_by", nullable = false)
    private String createdBy; // Keycloak user ID of the teacher
    
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    private Project project;
    
    // Relationships
    @OneToMany(mappedBy = "task")
    private Set<TaskStudentAssignment> studentAssignments;
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private Set<Submission> submissions;
}
