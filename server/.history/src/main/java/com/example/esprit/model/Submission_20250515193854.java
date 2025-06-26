package com.example.esprit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "submission_date")
    private LocalDateTime submissionDate;
    
    private Float grade;
    
    @Column(columnDefinition = "TEXT")
    private String feedback;
    
    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;
    
    @Column(name = "submitted_by", nullable = false)
    private String submittedBy; // Keycloak user ID of the student
    
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    
    @Column(name = "graded_by")
    private String gradedBy; // Keycloak user ID of the teacher who graded
    
    @Column(name = "graded_date")
    private LocalDateTime gradedDate;
}
