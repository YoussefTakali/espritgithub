package esprit.git.gestionsummary_backgit_me.Represotory;

import esprit.git.gestionsummary_backgit_me.Entities.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface Crep extends JpaRepository<Classe,Long> {
}
