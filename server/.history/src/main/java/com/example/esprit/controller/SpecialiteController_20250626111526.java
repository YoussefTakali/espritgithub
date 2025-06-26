package com.example.esprit.controller;

import com.example.esprit.model.Specialite;
import com.example.esprit.model.Specialites;
import com.example.esprit.model.TypeFormation;
import com.example.esprit.service.SpecialiteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;mple.esprit.controller;

import com.example.esprit.model.Specialite;
import com.example.esprit.service.SpecialiteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/specialites")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class SpecialiteController {

    private static final Logger logger = LoggerFactory.getLogger(SpecialiteController.class);

    private final SpecialiteService specialiteService;

    @GetMapping
    public ResponseEntity<Page<Specialite>> getAllSpecialites(Pageable pageable) {
        logger.info("Getting all specialites with pagination");
        try {
            Page<Specialite> specialites = specialiteService.getAllSpecialites(pageable);
            return ResponseEntity.ok(specialites);
        } catch (Exception e) {
            logger.error("Error getting specialites: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Specialite> getSpecialiteById(@PathVariable Long id) {
        logger.info("Getting specialite by id: {}", id);
        try {
            Optional<Specialite> specialite = specialiteService.getSpecialiteById(id);
            return specialite.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error getting specialite {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Specialite> createSpecialite(@RequestBody Map<String, Object> payload) {
        logger.info("Creating new specialite with payload: {}", payload);
        try {
            Specialite createdSpecialite = specialiteService.createSpecialite(payload);
            return ResponseEntity.ok(createdSpecialite);
        } catch (Exception e) {
            logger.error("Error creating specialite: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Specialite> updateSpecialite(@PathVariable Long id, @RequestBody Specialite specialite) {
        logger.info("Updating specialite with id: {}", id);
        try {
            Specialite updatedSpecialite = specialiteService.updateSpecialite(id, specialite);
            return ResponseEntity.ok(updatedSpecialite);
        } catch (Exception e) {
            logger.error("Error updating specialite {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialite(@PathVariable Long id) {
        logger.info("Deleting specialite with id: {}", id);
        try {
            specialiteService.deleteSpecialite(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting specialite {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Specialite>> searchSpecialites(@RequestParam String search, Pageable pageable) {
        logger.info("Searching specialites with term: {}", search);
        try {
            Page<Specialite> specialites = specialiteService.searchSpecialites(search, pageable);
            return ResponseEntity.ok(specialites);
        } catch (Exception e) {
            logger.error("Error searching specialites: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/enum-values")
    public ResponseEntity<Map<String, Object>> getEnumValues() {
        logger.info("Getting enum values for specialites");
        try {
            Map<String, Object> enumValues = Map.of(
                "specialites", Arrays.stream(Specialites.values())
                    .map(s -> Map.of("value", s.name(), "displayName", s.getDisplayName()))
                    .toArray(),
                "typeFormation", Arrays.stream(TypeFormation.values())
                    .map(t -> Map.of("value", t.name(), "displayName", t.name()))
                    .toArray()
            );
            return ResponseEntity.ok(enumValues);
        } catch (Exception e) {
            logger.error("Error getting enum values: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
