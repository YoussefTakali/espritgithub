package esprit.git.gestionsummary_backgit_me.Service;

import esprit.git.gestionsummary_backgit_me.Entities.Classe;
import esprit.git.gestionsummary_backgit_me.Entities.Niveau;
import esprit.git.gestionsummary_backgit_me.Represotory.Crep;
import esprit.git.gestionsummary_backgit_me.Represotory.Nrep;
import org.springframework.beans.factory.annotation.Autowired;
@org.springframework.stereotype.Service
public class Imp implements IService {
    @Autowired
    Crep crep;
    @Autowired
    Nrep nrep;

    @Override
    public Niveau ajoutNiveau(Niveau n) {
        return nrep.save(n);
    }

    @Override
    public Classe ajoutClasse(Classe c) {
        return crep.save(c);
    }
}
