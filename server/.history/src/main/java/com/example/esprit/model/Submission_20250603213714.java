package com.example.esprit.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
// Grade of the submission (optional)
    @Column
    private Double grade;

    // The group submitting (for group submission)
    @ManyToOne
@JoinColumn(name = "group_id", foreignKey = @ForeignKey(name = "fk_submission_group"))    @OnDelete(action = OnDeleteAction.CASCADE)
    private Group group;

    // Submission content (e.g., file URL, text, etc.)
    @Column(nullable = false)
    private String content;
    @Column(name = "description")
    private String description;
    // Timestamp of submission
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    // Status of the submission (optional)
    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;
}
