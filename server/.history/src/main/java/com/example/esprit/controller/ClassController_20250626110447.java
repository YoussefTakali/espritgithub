package com.example.esprit.controller;

import com.example.esprit.dto.ClassDTOResponse;
import com.example.esprit.model.Classe;
import com.example.esprit.model.Niveau;
import com.example.esprit.service.ClassService;
import com.example.esprit.service.IService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ClassController {

    private static final Logger logger = LoggerFactory.getLogger(ClassController.class);
    
    private final ClassService classService;
    private final IService iService;

    // Original endpoint - keep for backward compatibility
    @GetMapping("/by-teacher/{teacherId}")
    public ResponseEntity<List<ClassDTOResponse>> getClassesWithProjectsByTeacher(@PathVariable String teacherId) {
        List<ClassDTOResponse> response = classService.getClassesWithProjectsByTeacherId(teacherId);
        return ResponseEntity.ok(response);
    }

    // Integrated endpoints from shared controller
    @GetMapping("/niveau/{niveauId}")
    public ResponseEntity<Page<Classe>> getClassesByNiveau(
            @PathVariable Long niveauId,
            Pageable pageable) {
        
        logger.info("Getting classes for niveau: {}", niveauId);
        try {
            Page<Classe> classes = iService.getClassesByNiveau(niveauId, pageable);
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            logger.error("Error getting classes for niveau {}: {}", niveauId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/niveau/{niveauId}/all")
    public ResponseEntity<List<Classe>> getAllClassesByNiveau(@PathVariable Long niveauId) {
        logger.info("Getting all classes for niveau: {}", niveauId);
        try {
            List<Classe> classes = iService.getAllClassesByNiveau(niveauId);
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            logger.error("Error getting all classes for niveau {}: {}", niveauId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Classe> createClasse(@RequestBody Map<String, Object> payload) {
        logger.info("Creating new classe with payload: {}", payload);
        try {
            Classe createdClasse = iService.createClasse(payload);
            return ResponseEntity.ok(createdClasse);
        } catch (Exception e) {
            logger.error("Error creating classe: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Classe> updateClasse(
            @PathVariable Long id,
            @RequestBody Classe classe) {
        
        logger.info("Updating classe with id: {}", id);
        try {
            Classe updatedClasse = iService.updateClasse(id, classe);
            return ResponseEntity.ok(updatedClasse);
        } catch (Exception e) {
            logger.error("Error updating classe {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClasse(@PathVariable Long id) {
        logger.info("Deleting classe with id: {}", id);
        try {
            iService.deleteClasse(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting classe {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classe> getClasseById(@PathVariable Long id) {
        logger.info("Getting classe by id: {}", id);
        try {
            return iService.getClasseById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error getting classe {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Classe>> searchClasses(
            @RequestParam(required = false) Long niveauId,
            @RequestParam(required = false) String searchTerm,
            Pageable pageable) {
        
        logger.info("Searching classes with niveauId: {} and searchTerm: {}", niveauId, searchTerm);
        try {
            Page<Classe> classes = iService.searchClasses(niveauId, searchTerm, pageable);
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            logger.error("Error searching classes: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

