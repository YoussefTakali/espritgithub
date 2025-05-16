package com.example.esprit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_student_enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectStudentEnrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id", nullable = false)
    private String studentId; // Keycloak user ID
    
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate;
    
    @Enumerated(EnumType.STRING)
    private ProjectEnrollmentStatus status;
}
enum ProjectEnrollmentStatus {
    ACTIVE,
    COMPLETED,
    WITHDRAWN
}