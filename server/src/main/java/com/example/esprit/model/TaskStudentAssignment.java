package com.example.esprit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "task_student_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStudentAssignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id", nullable = false)
    private String studentId; // Keycloak user ID
    
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    @JsonIgnore
    private Task task;
    
    @Column(name = "assigned_date")
    private LocalDateTime assignedDate;
    
    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;
}
