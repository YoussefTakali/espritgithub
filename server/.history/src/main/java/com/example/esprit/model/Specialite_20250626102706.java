package com.example.esprit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Specialite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @JsonProperty("specialites")
    private Specialites specialites;

    @Enumerated(EnumType.STRING)
    @JsonProperty("typeFormation")
    private TypeFormation typeFormation;

    @OneToMany(mappedBy = "specialite", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "specialite-niveaux")
    @Builder.Default
    private List<Niveau> niveaux = new ArrayList<>();

    // Méthode utilitaire pour ajouter un niveau
    public void addNiveau(Niveau niveau) {
        niveaux.add(niveau);
        niveau.setSpecialite(this);
    }

    // Méthode utilitaire pour supprimer un niveau
    public void removeNiveau(Niveau niveau) {
        niveaux.remove(niveau);
        niveau.setSpecialite(null);
    }
}
