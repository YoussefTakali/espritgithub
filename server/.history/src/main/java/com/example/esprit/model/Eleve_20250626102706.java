package com.example.esprit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Eleve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String numeroEtudiant;
    private String telephone;

    @ManyToOne
    @JoinColumn(name = "classe_id")
    @JsonBackReference(value = "classe-eleves")
    private Classe classe;

    @OneToMany(mappedBy = "eleve")
    @Builder.Default
    private List<Repos> repos = new ArrayList<>();
}
