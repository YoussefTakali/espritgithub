package com.example.esprit.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"associatedClasses", "groups", "tasks"})
@ToString(exclude = {"associatedClasses", "groups", "tasks"})
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;
    
    @Column(name = "created_by", nullable = false)
    private String createdBy; // Keycloak user ID of the owner

    // Collaborators storage (other teachers)
    @ElementCollection
    @CollectionTable(
        name = "project_collaborators",
        joinColumns = @JoinColumn(name = "project_id")
    )
    @Column(name = "teacher_id")
    @Builder.Default
    private Set<String> collaborators = new HashSet<>();

    // Associated classes (many-to-many)
    @ManyToMany
    @JoinTable(
        name = "project_class",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    @Builder.Default
    private Set<Class> associatedClasses = new HashSet<>();

    // Groups in this project
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Group> groups = new HashSet<>();

    // Tasks in this project
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private Set<Task> tasks = new HashSet<>();
}