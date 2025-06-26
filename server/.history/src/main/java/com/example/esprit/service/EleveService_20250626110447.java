package com.example.esprit.service;

import com.example.esprit.model.Eleve;
import com.example.esprit.model.Classe;
import com.example.esprit.repository.EleveRepository;
import com.example.esprit.repository.ClasseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EleveService {

    private final EleveRepository eleveRepository;
    private final ClasseRepository classeRepository;

    public Eleve createEleve(Map<String, Object> payload) {
        Eleve eleve = new Eleve();
        eleve.setNom((String) payload.get("nom"));
        eleve.setPrenom((String) payload.get("prenom"));
        eleve.setEmail((String) payload.get("email"));
        eleve.setNumeroEtudiant((String) payload.get("numeroEtudiant"));
        eleve.setTelephone((String) payload.get("telephone"));
        
        if (payload.get("classeId") != null) {
            Long classeId = Long.valueOf(payload.get("classeId").toString());
            Classe classe = classeRepository.findById(classeId)
                    .orElseThrow(() -> new RuntimeException("Classe not found with id: " + classeId));
            eleve.setClasse(classe);
        }
        
        return eleveRepository.save(eleve);
    }

    public Eleve updateEleve(Long id, Eleve eleve) {
        Eleve existingEleve = eleveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Eleve not found with id: " + id));
        
        existingEleve.setNom(eleve.getNom());
        existingEleve.setPrenom(eleve.getPrenom());
        existingEleve.setEmail(eleve.getEmail());
        existingEleve.setNumeroEtudiant(eleve.getNumeroEtudiant());
        existingEleve.setTelephone(eleve.getTelephone());
        existingEleve.setClasse(eleve.getClasse());
        
        return eleveRepository.save(existingEleve);
    }

    public void deleteEleve(Long id) {
        eleveRepository.deleteById(id);
    }

    public List<Eleve> getAllEleves() {
        return eleveRepository.findAll();
    }

    public List<Eleve> getElevesByClasse(Long classeId) {
        return eleveRepository.findByClasseId(classeId);
    }

    public Page<Eleve> getElevesByClassePage(Long classeId, Pageable pageable) {
        return eleveRepository.findByClasseId(classeId, pageable);
    }

    public Eleve getEleveById(Long id) {
        return eleveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Eleve not found with id: " + id));
    }

    public Page<Eleve> searchEleves(Long classeId, String search, Pageable pageable) {
        return eleveRepository.findByClasseIdAndSearch(classeId, search, pageable);
    }
}
