package com.example.esprit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Repos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String motif;
    private String statut; // EN_ATTENTE, APPROUVE, REFUSE

    @ManyToOne
    @JoinColumn(name = "eleve_id")
    @JsonBackReference
    private Eleve eleve;

    @ManyToOne
    @JoinColumn(name = "classe_id")
    @JsonBackReference(value = "classe-repos")
    private Classe classe;
}
