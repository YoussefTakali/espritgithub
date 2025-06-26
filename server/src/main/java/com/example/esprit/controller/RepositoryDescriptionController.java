package com.example.esprit.controller;

import com.example.esprit.model.RepositoryDescription;
import com.example.esprit.service.RepositoryDescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/repo-descriptions")
@RequiredArgsConstructor
public class RepositoryDescriptionController {

    private final RepositoryDescriptionService repositoryDescriptionService;

    /**
     * Get repository description
     * GET /api/repo-descriptions/{owner}/{repo}
     */
    @GetMapping("/{owner}/{repo}")
    public ResponseEntity<Map<String, Object>> getDescription(
            @PathVariable String owner,
            @PathVariable String repo) {
        
        Optional<RepositoryDescription> description = repositoryDescriptionService.getDescription(owner, repo);
        Map<String, Object> response = new HashMap<>();
        
        if (description.isPresent()) {
            response.put("success", true);
            response.put("data", description.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Repository description not found");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Save or update repository description
     * POST /api/repo-descriptions/{owner}/{repo}
     * Body: {"description": "text", "username": "user"}
     */
    @PostMapping("/{owner}/{repo}")
    public ResponseEntity<Map<String, Object>> saveDescription(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestBody Map<String, String> requestBody) {
        
        try {
            String description = requestBody.get("description");
            String username = requestBody.get("username");
            
            if (description == null || username == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Description and username are required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            RepositoryDescription saved = repositoryDescriptionService.saveDescription(owner, repo, description, username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", saved);
            response.put("message", "Repository description saved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error saving repository description: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Delete repository description
     * DELETE /api/repo-descriptions/{owner}/{repo}
     */
    @DeleteMapping("/{owner}/{repo}")
    public ResponseEntity<Map<String, Object>> deleteDescription(
            @PathVariable String owner,
            @PathVariable String repo) {
        
        try {
            boolean deleted = repositoryDescriptionService.deleteDescription(owner, repo);
            Map<String, Object> response = new HashMap<>();
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "Repository description deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Repository description not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error deleting repository description: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Check if repository description exists
     * GET /api/repo-descriptions/{owner}/{repo}/exists
     */
    @GetMapping("/{owner}/{repo}/exists")
    public ResponseEntity<Map<String, Object>> checkDescriptionExists(
            @PathVariable String owner,
            @PathVariable String repo) {
        
        boolean exists = repositoryDescriptionService.exists(owner, repo);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all repository descriptions by owner
     * GET /api/repo-descriptions/{owner}
     */
    @GetMapping("/{owner}")
    public ResponseEntity<Map<String, Object>> getDescriptionsByOwner(
            @PathVariable String owner) {
        
        try {
            List<RepositoryDescription> descriptions = repositoryDescriptionService.getDescriptionsByOwner(owner);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", descriptions);
            response.put("count", descriptions.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving repository descriptions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get repository descriptions created by a user
     * GET /api/repo-descriptions/created-by/{username}
     */
    @GetMapping("/created-by/{username}")
    public ResponseEntity<Map<String, Object>> getDescriptionsCreatedBy(
            @PathVariable String username) {
        
        try {
            List<RepositoryDescription> descriptions = repositoryDescriptionService.getDescriptionsCreatedBy(username);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", descriptions);
            response.put("count", descriptions.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving repository descriptions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
