package esprit.git.gestionsummary_backgit_me.Controller;

import esprit.git.gestionsummary_backgit_me.Entities.Classe;
import esprit.git.gestionsummary_backgit_me.Entities.Niveau;
import esprit.git.gestionsummary_backgit_me.Service.IService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gestionSummary")
@AllArgsConstructor
public class Classecontroller {
    @PostMapping("/addniveau")
     public Niveau addniveau(@RequestBody Niveau n){
        return iservice.ajoutNiveau(n);
    }

    IService iservice;
    @PostMapping("/addclasse")
    public Classe addclasse(@RequestBody Classe c){
        return iservice.ajoutClasse(c);
    }
}
