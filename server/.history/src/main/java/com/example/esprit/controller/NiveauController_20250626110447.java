package com.example.esprit.controller;

import com.example.esprit.model.Niveau;
import com.example.esprit.service.NiveauService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/niveaux")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class NiveauController {

    private static final Logger logger = LoggerFactory.getLogger(NiveauController.class);

    private final NiveauService niveauService;

    @GetMapping("/specialite/{specialiteId}")
    public ResponseEntity<Page<Niveau>> getNiveauxBySpecialite(@PathVariable Long specialiteId, Pageable pageable) {
        logger.info("Getting niveaux for specialite: {}", specialiteId);
        try {
            Page<Niveau> niveaux = niveauService.getNiveauxBySpecialite(specialiteId, pageable);
            return ResponseEntity.ok(niveaux);
        } catch (Exception e) {
            logger.error("Error getting niveaux for specialite {}: {}", specialiteId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/specialite/{specialiteId}/search")
    public ResponseEntity<Page<Niveau>> searchNiveaux(@PathVariable Long specialiteId, @RequestParam String search, Pageable pageable) {
        logger.info("Searching niveaux for specialite {} with search term: {}", specialiteId, search);
        try {
            Page<Niveau> niveaux = niveauService.searchNiveaux(specialiteId, search, pageable);
            return ResponseEntity.ok(niveaux);
        } catch (Exception e) {
            logger.error("Error searching niveaux: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Niveau> getNiveauById(@PathVariable Long id) {
        logger.info("Getting niveau by id: {}", id);
        try {
            Niveau niveau = niveauService.getNiveauById(id);
            return ResponseEntity.ok(niveau);
        } catch (Exception e) {
            logger.error("Error getting niveau {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Niveau> createNiveau(@RequestBody Map<String, Object> payload) {
        logger.info("Creating new niveau with payload: {}", payload);
        try {
            Niveau createdNiveau = niveauService.createNiveau(payload);
            return ResponseEntity.ok(createdNiveau);
        } catch (Exception e) {
            logger.error("Error creating niveau: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Niveau> updateNiveau(@PathVariable Long id, @RequestBody Niveau niveau) {
        logger.info("Updating niveau with id: {}", id);
        try {
            Niveau updatedNiveau = niveauService.updateNiveau(id, niveau);
            return ResponseEntity.ok(updatedNiveau);
        } catch (Exception e) {
            logger.error("Error updating niveau {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNiveau(@PathVariable Long id) {
        logger.info("Deleting niveau with id: {}", id);
        try {
            niveauService.deleteNiveau(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting niveau {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
