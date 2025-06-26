package com.example.esprit.service;

import com.example.esprit.model.Specialite;
import com.example.esprit.model.Specialites;
import com.example.esprit.model.TypeFormation;
import com.example.esprit.repository.SpecialiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpecialiteService {

    private final SpecialiteRepository specialiteRepository;

    public Page<Specialite> getAllSpecialites(Pageable pageable) {
        return specialiteRepository.findAll(pageable);
    }

    public Optional<Specialite> getSpecialiteById(Long id) {
        return specialiteRepository.findById(id);
    }

    public Specialite saveSpecialite(Specialite specialite) {
        return specialiteRepository.save(specialite);
    }

    public Specialite createSpecialite(Map<String, Object> payload) {
        Specialite specialite = new Specialite();
        specialite.setNom((String) payload.get("nom"));

        if (payload.get("specialites") != null) {
            try {
                String specialiteString = payload.get("specialites").toString();
                specialite.setSpecialites(Specialites.fromString(specialiteString));
            } catch (Exception e) {
                // Default to TRANC_COMMUN if invalid value
                specialite.setSpecialites(Specialites.TRANC_COMMUN);
            }
        }

        if (payload.get("typeFormation") != null) {
            try {
                specialite.setTypeFormation(TypeFormation.valueOf(payload.get("typeFormation").toString()));
            } catch (IllegalArgumentException e) {
                // Default to COURS_DU_JOUR if invalid value
                specialite.setTypeFormation(TypeFormation.COURS_DU_JOUR);
            }
        }

        return specialiteRepository.save(specialite);
    }

    public Specialite updateSpecialite(Long id, Specialite specialite) {
        Specialite existingSpecialite = specialiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialite not found with id: " + id));

        existingSpecialite.setNom(specialite.getNom());
        existingSpecialite.setSpecialites(specialite.getSpecialites());
        existingSpecialite.setTypeFormation(specialite.getTypeFormation());

        return specialiteRepository.save(existingSpecialite);
    }

    public void deleteSpecialite(Long id) {
        specialiteRepository.deleteById(id);
    }

    public Page<Specialite> searchSpecialites(String search, Pageable pageable) {
        return specialiteRepository.findBySpecialitesContainingIgnoreCase(search, pageable);
    }
}
