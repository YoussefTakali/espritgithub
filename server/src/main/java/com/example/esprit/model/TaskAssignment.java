package com.example.esprit.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task_assignments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignment {
    public enum AssignmentScope {
        PROJECT,    // All groups in all classes
        CLASS,      // All groups in one class
        GROUP,      // Specific group
        INDIVIDUAL  // Specific student
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    
    @Enumerated(EnumType.STRING)
    private AssignmentScope scope;
    
    // Only used when scope = CLASS or GROUP
    @ManyToOne
    @JoinColumn(name = "class_id")
    private Class classe;
    
    // Only used when scope = GROUP
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    
    // Only used when scope = INDIVIDUAL
    @Column(name = "student_id")
    private String studentId;
}