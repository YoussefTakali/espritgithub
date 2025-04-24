package esprit.git.gestionsummary_backgit_me.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Niveau {
    @ManyToOne
    Specialite specialite;
    @OneToMany(mappedBy = "niveau")
    List<Classe> classes;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private Annee annee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Annee getAnnee() {
        return annee;
    }

    public void setAnnee(Annee annee) {
        this.annee = annee;
    }
}


