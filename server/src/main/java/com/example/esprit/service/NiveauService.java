package com.example.esprit.service;

import com.example.esprit.model.Niveau;
import com.example.esprit.model.Specialite;
import com.example.esprit.model.Annee;
import com.example.esprit.repository.NiveauRepository;
import com.example.esprit.repository.SpecialiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NiveauService {

    private final NiveauRepository niveauRepository;
    private final SpecialiteRepository specialiteRepository;

    public Page<Niveau> getNiveauxBySpecialite(Long specialiteId, Pageable pageable) {
        return niveauRepository.findBySpecialiteId(specialiteId, pageable);
    }

    public Page<Niveau> searchNiveaux(Long specialiteId, String search, Pageable pageable) {
        return niveauRepository.findBySpecialiteIdAndAnneeContainingIgnoreCase(specialiteId, search, pageable);
    }

    public Niveau getNiveauById(Long id) {
        return niveauRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Niveau not found with id: " + id));
    }

    public Niveau createNiveau(Map<String, Object> payload) {
        Niveau niveau = new Niveau();
        
        if (payload.get("annee") != null) {
            niveau.setAnnee(Annee.valueOf(payload.get("annee").toString()));
        }
        
        if (payload.get("specialiteId") != null) {
            Long specialiteId = Long.valueOf(payload.get("specialiteId").toString());
            Specialite specialite = specialiteRepository.findById(specialiteId)
                    .orElseThrow(() -> new RuntimeException("Specialite not found with id: " + specialiteId));
            niveau.setSpecialite(specialite);
        }
        
        return niveauRepository.save(niveau);
    }

    public Niveau updateNiveau(Long id, Niveau niveau) {
        Niveau existingNiveau = niveauRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Niveau not found with id: " + id));
        
        existingNiveau.setAnnee(niveau.getAnnee());
        existingNiveau.setSpecialite(niveau.getSpecialite());
        
        return niveauRepository.save(existingNiveau);
    }

    public void deleteNiveau(Long id) {
        niveauRepository.deleteById(id);
    }

    public List<Niveau> getAllNiveauxBySpecialite(Long specialiteId) {
        return niveauRepository.findBySpecialiteId(specialiteId);
    }
}
