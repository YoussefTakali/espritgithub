package com.example.esprit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Classe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "niveau_id", nullable = false)
    @JsonBackReference(value = "niveau-classes")
    private Niveau niveau;

    @OneToMany(mappedBy = "classe")
    @JsonManagedReference(value = "classe-eleves")
    @Builder.Default
    private List<Eleve> eleves = new ArrayList<>();

    @OneToMany(mappedBy = "classe")
    @JsonManagedReference(value = "classe-repos")
    @Builder.Default
    private List<Repos> repos = new ArrayList<>();
}
