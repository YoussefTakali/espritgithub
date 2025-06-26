package com.example.esprit.controller;

import com.example.esprit.model.Repos;
import com.example.esprit.service.ReposService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ReposController {

    private final ReposService reposService;

    @PostMapping
    public ResponseEntity<Repos> createRepos(@RequestBody Repos repos) {
        Repos createdRepos = reposService.createRepos(repos);
        return ResponseEntity.ok(createdRepos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Repos> updateRepos(@PathVariable Long id, @RequestBody Repos repos) {
        try {
            Repos updatedRepos = reposService.updateRepos(id, repos);
            return ResponseEntity.ok(updatedRepos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRepos(@PathVariable Long id) {
        try {
            reposService.deleteRepos(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Repos>> getAllRepos() {
        List<Repos> repos = reposService.getAllRepos();
        return ResponseEntity.ok(repos);
    }

    @GetMapping("/classe/{classeId}")
    public ResponseEntity<List<Repos>> getReposByClasse(@PathVariable Long classeId) {
        List<Repos> repos = reposService.getReposByClasse(classeId);
        return ResponseEntity.ok(repos);
    }

    @GetMapping("/eleve/{eleveId}")
    public ResponseEntity<List<Repos>> getReposByEleve(@PathVariable Long eleveId) {
        List<Repos> repos = reposService.getReposByEleve(eleveId);
        return ResponseEntity.ok(repos);
    }

    @GetMapping("/classe/{classeId}/page")
    public ResponseEntity<Page<Repos>> getReposByClassePage(
            @PathVariable Long classeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Repos> repos = reposService.getReposByClassePage(classeId, pageable);
        return ResponseEntity.ok(repos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Repos> getReposById(@PathVariable Long id) {
        try {
            Repos repos = reposService.getReposById(id);
            return ResponseEntity.ok(repos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
