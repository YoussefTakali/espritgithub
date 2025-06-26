package esprit.git.gestionsummary_backgit_me.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Specialite {
    @OneToMany(mappedBy = "specialite")
    List<Niveau> niveaux;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Specialites specialites;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Specialites getSpecialites() {
        return specialites;
    }

    public void setSpecialites(Specialites specialites) {
        this.specialites = specialites;
    }
}
