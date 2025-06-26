package com.example.esprit.service;

import com.example.esprit.model.Classe;
import com.example.esprit.model.Niveau;
import com.example.esprit.repository.ClasseRepository;
import com.example.esprit.repository.NiveauRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IServiceImpl implements IService {

    private static final Logger logger = LoggerFactory.getLogger(IServiceImpl.class);

    private final ClasseRepository classeRepository;
    private final NiveauRepository niveauRepository;

    @Override
    public Page<Classe> getClassesByNiveau(Long niveauId, Pageable pageable) {
        logger.info("Getting classes by niveau ID: {}", niveauId);
        return classeRepository.findByNiveauId(niveauId, pageable);
    }

    @Override
    public List<Classe> getAllClassesByNiveau(Long niveauId) {
        logger.info("Getting all classes by niveau ID: {}", niveauId);
        return classeRepository.findByNiveauId(niveauId);
    }

    @Override
    public Classe saveClasse(Classe classe) {
        logger.info("Saving classe: {}", classe.getNumber());
        return classeRepository.save(classe);
    }

    @Override
    public Classe createClasse(Map<String, Object> payload) {
        logger.info("Creating classe from payload: {}", payload);
        
        try {
            String number = (String) payload.get("number");
            Integer capacity = (Integer) payload.get("capacity");
            Long niveauId = Long.valueOf(payload.get("niveauId").toString());

            if (number == null || capacity == null || niveauId == null) {
                throw new IllegalArgumentException("Missing required fields: number, capacity, or niveauId");
            }

            // Find the niveau
            Niveau niveau = niveauRepository.findById(niveauId)
                    .orElseThrow(() -> new IllegalArgumentException("Niveau not found with ID: " + niveauId));

            // Create the classe
            Classe classe = Classe.builder()
                    .number(number)
                    .capacity(capacity)
                    .niveau(niveau)
                    .build();

            return classeRepository.save(classe);
        } catch (Exception e) {
            logger.error("Error creating classe: {}", e.getMessage());
            throw new RuntimeException("Failed to create classe", e);
        }
    }

    @Override
    public Classe updateClasse(Long id, Classe classe) {
        logger.info("Updating classe with ID: {}", id);
        
        return classeRepository.findById(id)
                .map(existingClasse -> {
                    existingClasse.setNumber(classe.getNumber());
                    existingClasse.setCapacity(classe.getCapacity());
                    if (classe.getNiveau() != null) {
                        existingClasse.setNiveau(classe.getNiveau());
                    }
                    return classeRepository.save(existingClasse);
                })
                .orElseThrow(() -> new RuntimeException("Classe not found with ID: " + id));
    }

    @Override
    public void deleteClasse(Long id) {
        logger.info("Deleting classe with ID: {}", id);
        
        if (classeRepository.existsById(id)) {
            classeRepository.deleteById(id);
        } else {
            throw new RuntimeException("Classe not found with ID: " + id);
        }
    }

    @Override
    public Optional<Classe> getClasseById(Long id) {
        logger.info("Getting classe by ID: {}", id);
        return classeRepository.findById(id);
    }

    @Override
    public Page<Classe> searchClasses(Long niveauId, String searchTerm, Pageable pageable) {
        logger.info("Searching classes with niveauId: {} and searchTerm: {}", niveauId, searchTerm);
        
        if (niveauId != null && searchTerm != null && !searchTerm.trim().isEmpty()) {
            return classeRepository.findByNiveauIdAndNumberContainingIgnoreCase(niveauId, searchTerm.trim(), pageable);
        } else if (niveauId != null) {
            return classeRepository.findByNiveauId(niveauId, pageable);
        } else {
            return classeRepository.findAll(pageable);
        }
    }
}
