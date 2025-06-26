package com.example.esprit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "student_class_enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentClassEnrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id", nullable = false, unique = true)
    private String studentId; // Keycloak user ID - unique constraint as a student can only be in one class
    
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    @JsonIgnore
    private Class enrolledClass;
    
    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate;
    
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;
}
