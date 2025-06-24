package esprit.git_push_test.Controller;

import esprit.git_push_test.Entity.RepositoryDescription;
import esprit.git_push_test.Service.RepositoryDescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/repo-descriptions")
@CrossOrigin(origins = "http://localhost:4200")
public class RepositoryDescriptionController {

    @Autowired
    private RepositoryDescriptionService repositoryDescriptionService;

    /**
     * Get repository description
     * GET /api/repo-descriptions/{owner}/{repo}
     */
    @GetMapping("/{owner}/{repo}")
    public ResponseEntity<Map<String, Object>> getDescription(
            @PathVariable String owner,
            @PathVariable String repo) {
        
        try {
            Optional<RepositoryDescription> description = repositoryDescriptionService.getDescription(owner, repo);
            
            if (description.isPresent()) {
                RepositoryDescription repoDesc = description.get();
                Map<String, Object> response = new HashMap<>();
                response.put("id", repoDesc.getId());
                response.put("owner", repoDesc.getOwner());
                response.put("repositoryName", repoDesc.getRepositoryName());
                response.put("description", repoDesc.getDescription());
                response.put("createdAt", repoDesc.getCreatedAt());
                response.put("updatedAt", repoDesc.getUpdatedAt());
                response.put("createdBy", repoDesc.getCreatedBy());
                response.put("updatedBy", repoDesc.getUpdatedBy());
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error fetching repository description: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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
            
            if (description == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Description is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (username == null || username.trim().isEmpty()) {
                username = "anonymous";
            }
            
            RepositoryDescription savedDescription = repositoryDescriptionService.saveDescription(
                    owner, repo, description, username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedDescription.getId());
            response.put("owner", savedDescription.getOwner());
            response.put("repositoryName", savedDescription.getRepositoryName());
            response.put("description", savedDescription.getDescription());
            response.put("createdAt", savedDescription.getCreatedAt());
            response.put("updatedAt", savedDescription.getUpdatedAt());
            response.put("createdBy", savedDescription.getCreatedBy());
            response.put("updatedBy", savedDescription.getUpdatedBy());
            response.put("message", "Repository description saved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error saving repository description: " + e.getMessage());
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
                response.put("message", "Repository description deleted successfully");
                response.put("deleted", true);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Repository description not found");
                response.put("deleted", false);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error deleting repository description: " + e.getMessage());
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
        
        try {
            boolean exists = repositoryDescriptionService.hasDescription(owner, repo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("exists", exists);
            response.put("owner", owner);
            response.put("repositoryName", repo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error checking repository description existence: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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
            long count = repositoryDescriptionService.getDescriptionCountByOwner(owner);
            
            Map<String, Object> response = new HashMap<>();
            response.put("owner", owner);
            response.put("count", count);
            response.put("descriptions", descriptions);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error fetching repository descriptions: " + e.getMessage());
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
            response.put("username", username);
            response.put("count", descriptions.size());
            response.put("descriptions", descriptions);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error fetching repository descriptions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get repository descriptions updated by a user
     * GET /api/repo-descriptions/updated-by/{username}
     */
    @GetMapping("/updated-by/{username}")
    public ResponseEntity<Map<String, Object>> getDescriptionsUpdatedBy(
            @PathVariable String username) {
        
        try {
            List<RepositoryDescription> descriptions = repositoryDescriptionService.getDescriptionsUpdatedBy(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("count", descriptions.size());
            response.put("descriptions", descriptions);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error fetching repository descriptions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
