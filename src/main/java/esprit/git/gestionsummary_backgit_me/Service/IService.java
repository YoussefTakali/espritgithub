package esprit.git.gestionsummary_backgit_me.Service;

import esprit.git.gestionsummary_backgit_me.Entities.Classe;
import esprit.git.gestionsummary_backgit_me.Entities.Niveau;

public interface IService {
    Niveau ajoutNiveau(Niveau n);
    Classe ajoutClasse(Classe c);
}
