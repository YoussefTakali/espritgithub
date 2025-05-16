package com.example.esprit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Class {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(name = "academic_year")
    private String academicYear;
    
    private String subject;
    
    // Relationships
    @OneToMany(mappedBy = "teachingClass")
    @JsonIgnore
    private Set<TeacherClassAssignment> teacherAssignments;
    
    // One-to-many with StudentClassEnrollment - a class can have many students
    @OneToMany(mappedBy = "enrolledClass")
    @JsonIgnore  // ignore to avoid serialization issues
    private Set<StudentClassEnrollment> studentEnrollments;
    
    @OneToMany(mappedBy = "associatedClass")
    @JsonIgnore  // ignore projects to avoid recursive serialization
    private Set<Project> projects;
}
