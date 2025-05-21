package com.example.esprit.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
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
@Table(name = "groups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "class_id", nullable = false)
    private Class classe;
    
    @ElementCollection
    @CollectionTable(
        name = "group_members",
        joinColumns = @JoinColumn(name = "group_id")
    )
    @Column(name = "student_id")
    private Set<String> memberIds = new HashSet<>();
}