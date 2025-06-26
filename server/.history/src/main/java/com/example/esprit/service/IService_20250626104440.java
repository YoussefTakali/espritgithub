package com.example.esprit.service;

import com.example.esprit.model.Classe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IService {
    // Classe methods
    Page<Classe> getClassesByNiveau(Long niveauId, Pageable pageable);
    List<Classe> getAllClassesByNiveau(Long niveauId);
    Classe saveClasse(Classe classe);
    void deleteClasse(Long id);
    Optional<Classe> getClasseById(Long id);
    Page<Classe> searchClasses(Long niveauId, String searchTerm, Pageable pageable);
}
