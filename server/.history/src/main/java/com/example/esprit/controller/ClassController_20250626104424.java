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

    // New endpoints from the shared controllers
    @GetMapping("/niveau/{niveauId}")
    public ResponseEntity<Page<Classe>> getClassesByNiveau(
            @PathVariable Long niveauId,
            Pageable pageable) {
        return ResponseEntity.ok(iService.getClassesByNiveau(niveauId, pageable));
    }

    @GetMapping("/niveau/{niveauId}/all")
    public ResponseEntity<List<Classe>> getAllClassesByNiveau(@PathVariable Long niveauId) {
        return ResponseEntity.ok(iService.getAllClassesByNiveau(niveauId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Classe> createClasse(@RequestBody Map<String, Object> payload) {
        logger.info("POST /api/classes - Received request to create classe: {}", payload);
        try {
            if (payload == null) {
                logger.error("POST /api/classes - Payload is null");
                return ResponseEntity.badRequest().build();
            }

            String number = (String) payload.get("number");
            if (number == null || number.trim().isEmpty()) {
                logger.error("POST /api/classes - Class number is null or empty");
                return ResponseEntity.badRequest().build();
            }

            Integer capacity = (Integer) payload.get("capacity");
            if (capacity == null || capacity <= 0) {
                logger.error("POST /api/classes - Capacity is null or invalid");
                return ResponseEntity.badRequest().build();
            }

            Map<String, Object> niveauMap = (Map<String, Object>) payload.get("niveau");
            if (niveauMap == null || !niveauMap.containsKey("id")) {
                logger.error("POST /api/classes - Niveau is null or has no ID");
                return ResponseEntity.badRequest().build();
            }

            Long niveauId = Long.valueOf(niveauMap.get("id").toString());
            
            Classe classe = Classe.builder()
                    .number(number.trim())
                    .capacity(capacity)
                    .niveau(Niveau.builder().id(niveauId).build())
                    .build();

            Classe savedClasse = iService.saveClasse(classe);
            logger.info("POST /api/classes - Successfully saved classe with id: {}", savedClasse.getId());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(savedClasse);
        } catch (Exception e) {
            logger.error("POST /api/classes - Error creating classe: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Classe> updateClasse(
            @PathVariable Long id,
            @RequestBody Classe classe) {
        classe.setId(id);
        return ResponseEntity.ok(iService.saveClasse(classe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClasse(@PathVariable Long id) {
        iService.deleteClasse(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classe> getClasseById(@PathVariable Long id) {
        return iService.getClasseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Classe>> searchClasses(
            @RequestParam(required = false) Long niveauId,
            @RequestParam(required = false) String searchTerm,
            Pageable pageable) {
        return ResponseEntity.ok(iService.searchClasses(niveauId, searchTerm, pageable));
    }
}

