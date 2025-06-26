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
public class Niveau {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Annee annee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialite_id")
    @JsonBackReference(value = "specialite-niveaux")
    private Specialite specialite;

    @OneToMany(mappedBy = "niveau", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "niveau-classes")
    @Builder.Default
    private List<Classe> classes = new ArrayList<>();
}
