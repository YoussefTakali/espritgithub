package com.example.esprit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "teacher_class_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherClassAssignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "teacher_id", nullable = false)
    private String teacherId; // Keycloak user ID
    
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    @JsonBackReference  // handle backward reference in JSON serialization
    private Class teachingClass;
    
    @Column(name = "assignment_date")
    private LocalDateTime assignmentDate;
    
    private String role; // e.g., "Primary", "Assistant", "Substitute"
}