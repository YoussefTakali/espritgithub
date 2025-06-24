package esprit.git_push_test.Service;

import esprit.git_push_test.Entity.RepositoryDescription;
import esprit.git_push_test.Repository.RepositoryDescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RepositoryDescriptionService {

    @Autowired
    private RepositoryDescriptionRepository repositoryDescriptionRepository;

    /**
     * Get repository description by owner and repository name
     * 
     * @param owner Repository owner
     * @param repositoryName Repository name
     * @return Optional RepositoryDescription
     */
    public Optional<RepositoryDescription> getDescription(String owner, String repositoryName) {
        try {
            return repositoryDescriptionRepository.findByOwnerAndRepositoryName(owner, repositoryName);
        } catch (Exception e) {
            System.err.println("Error getting repository description: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Save or update repository description
     * 
     * @param owner Repository owner
     * @param repositoryName Repository name
     * @param description Description text
     * @param username Username performing the operation
     * @return Saved RepositoryDescription
     */
    public RepositoryDescription saveDescription(String owner, String repositoryName, String description, String username) {
        try {
            Optional<RepositoryDescription> existingDescription = repositoryDescriptionRepository.findByOwnerAndRepositoryName(owner, repositoryName);
            
            RepositoryDescription repoDescription;
            
            if (existingDescription.isPresent()) {
                // Update existing description
                repoDescription = existingDescription.get();
                repoDescription.setDescription(description);
                repoDescription.setUpdatedBy(username);
                repoDescription.setUpdatedAt(LocalDateTime.now());
                
                System.out.println("Updating existing description for " + owner + "/" + repositoryName);
            } else {
                // Create new description
                repoDescription = new RepositoryDescription(owner, repositoryName, description, username);
                
                System.out.println("Creating new description for " + owner + "/" + repositoryName);
            }
            
            return repositoryDescriptionRepository.save(repoDescription);
        } catch (Exception e) {
            System.err.println("Error saving repository description: " + e.getMessage());
            throw new RuntimeException("Failed to save repository description", e);
        }
    }

    /**
     * Delete repository description by owner and repository name
     * 
     * @param owner Repository owner
     * @param repositoryName Repository name
     * @return true if deleted, false if not found
     */
    public boolean deleteDescription(String owner, String repositoryName) {
        try {
            int deletedCount = repositoryDescriptionRepository.deleteByOwnerAndRepositoryName(owner, repositoryName);
            
            if (deletedCount > 0) {
                System.out.println("Deleted description for " + owner + "/" + repositoryName);
                return true;
            } else {
                System.out.println("No description found to delete for " + owner + "/" + repositoryName);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting repository description: " + e.getMessage());
            throw new RuntimeException("Failed to delete repository description", e);
        }
    }

    /**
     * Check if repository description exists
     * 
     * @param owner Repository owner
     * @param repositoryName Repository name
     * @return true if exists, false otherwise
     */
    public boolean hasDescription(String owner, String repositoryName) {
        try {
            return repositoryDescriptionRepository.existsByOwnerAndRepositoryName(owner, repositoryName);
        } catch (Exception e) {
            System.err.println("Error checking repository description existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all repository descriptions by owner
     * 
     * @param owner Repository owner
     * @return List of RepositoryDescription
     */
    public List<RepositoryDescription> getDescriptionsByOwner(String owner) {
        try {
            return repositoryDescriptionRepository.findByOwnerOrderByUpdatedAtDesc(owner);
        } catch (Exception e) {
            System.err.println("Error getting repository descriptions by owner: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Get count of repository descriptions by owner
     * 
     * @param owner Repository owner
     * @return count of descriptions
     */
    public long getDescriptionCountByOwner(String owner) {
        try {
            return repositoryDescriptionRepository.countByOwner(owner);
        } catch (Exception e) {
            System.err.println("Error getting repository description count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get all repository descriptions created by a user
     * 
     * @param username Username
     * @return List of RepositoryDescription
     */
    public List<RepositoryDescription> getDescriptionsCreatedBy(String username) {
        try {
            return repositoryDescriptionRepository.findByCreatedByOrderByCreatedAtDesc(username);
        } catch (Exception e) {
            System.err.println("Error getting repository descriptions created by user: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Get all repository descriptions updated by a user
     * 
     * @param username Username
     * @return List of RepositoryDescription
     */
    public List<RepositoryDescription> getDescriptionsUpdatedBy(String username) {
        try {
            return repositoryDescriptionRepository.findByUpdatedByOrderByUpdatedAtDesc(username);
        } catch (Exception e) {
            System.err.println("Error getting repository descriptions updated by user: " + e.getMessage());
            return List.of();
        }
    }
}
