package esprit.git.gestionsummary_backgit_me.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "classe")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Classe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le numéro de classe ne peut pas être null")
    @Min(value = 1, message = "Le numéro de classe doit être supérieur à 0")
    private int number;

    @ManyToOne
    @JoinColumn(name = "niveau_id", nullable = false)
    @NotNull(message = "Le niveau ne peut pas être null")
    private Niveau niveau;
}
