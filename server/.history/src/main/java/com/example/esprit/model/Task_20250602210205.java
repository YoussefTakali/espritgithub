package com.example.esprit.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    private String createdBy;
    
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    // New task assignments (replaces studentAssignments)
    @OneToMany(mappedBy = "task", fetch = FetchType., cascade = CascadeType.ALL)
    @Builder.Default
    @JsonManagedReference
    private Set<TaskAssignment> assignments = new HashSet<>();
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Submission> submissions = new HashSet<>();
}