package com.example.esprit.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The task this submission belongs to
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    // The student submitting (for individual submission)
    @Column(name = "student_id")
    private String studentId;

    // The group submitting (for group submission)
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    // Submission content (e.g., file URL, text, etc.)
    @Column(nullable = false)
    private String content;

    // Timestamp of submission
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    // Status of the submission (optional)
    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;
}
