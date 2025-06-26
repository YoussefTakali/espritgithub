package com.example.esprit.controller;

import com.example.esprit.model.Eleve;
import com.example.esprit.service.EleveService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eleves")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EleveController {

    private static final Logger logger = LoggerFactory.getLogger(EleveController.class);

    private final EleveService eleveService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Eleve> createEleve(@RequestBody Map<String, Object> payload) {
        logger.info("Creating new eleve with payload: {}", payload);
        try {
            Eleve createdEleve = eleveService.createEleve(payload);
            return ResponseEntity.ok(createdEleve);
        } catch (Exception e) {
            logger.error("Error creating eleve: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Eleve> updateEleve(@PathVariable Long id, @RequestBody Eleve eleve) {
        logger.info("Updating eleve with id: {}", id);
        try {
            Eleve updatedEleve = eleveService.updateEleve(id, eleve);
            return ResponseEntity.ok(updatedEleve);
        } catch (Exception e) {
            logger.error("Error updating eleve {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEleve(@PathVariable Long id) {
        logger.info("Deleting eleve with id: {}", id);
        try {
            eleveService.deleteEleve(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting eleve {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Eleve>> getAllEleves() {
        logger.info("Getting all eleves");
        try {
            List<Eleve> eleves = eleveService.getAllEleves();
            return ResponseEntity.ok(eleves);
        } catch (Exception e) {
            logger.error("Error getting all eleves: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/classe/{classeId}")
    public ResponseEntity<List<Eleve>> getElevesByClasse(@PathVariable Long classeId) {
        logger.info("Getting eleves for classe: {}", classeId);
        try {
            List<Eleve> eleves = eleveService.getElevesByClasse(classeId);
            return ResponseEntity.ok(eleves);
        } catch (Exception e) {
            logger.error("Error getting eleves for classe {}: {}", classeId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/classe/{classeId}/page")
    public ResponseEntity<Page<Eleve>> getElevesByClassePage(
            @PathVariable Long classeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("Getting eleves for classe {} with pagination", classeId);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Eleve> eleves = eleveService.getElevesByClassePage(classeId, pageable);
            return ResponseEntity.ok(eleves);
        } catch (Exception e) {
            logger.error("Error getting eleves page for classe {}: {}", classeId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Eleve> getEleveById(@PathVariable Long id) {
        logger.info("Getting eleve by id: {}", id);
        try {
            Eleve eleve = eleveService.getEleveById(id);
            return ResponseEntity.ok(eleve);
        } catch (Exception e) {
            logger.error("Error getting eleve {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
