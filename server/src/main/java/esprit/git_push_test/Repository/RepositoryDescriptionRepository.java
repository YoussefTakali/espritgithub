package esprit.git_push_test.Repository;

import esprit.git_push_test.Entity.RepositoryDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryDescriptionRepository extends JpaRepository<RepositoryDescription, Long> {

    /**
     * Find repository description by owner and repository name
     * 
     * @param owner Repository owner
     * @param repositoryName Repository name
     * @return Optional RepositoryDescription
     */
    Optional<RepositoryDescription> findByOwnerAndRepositoryName(String owner, String repositoryName);

    /**
     * Check if a repository description exists for the given owner and repository name
     * 
     * @param owner Repository owner
     * @param repositoryName Repository name
     * @return true if exists, false otherwise
     */
    boolean existsByOwnerAndRepositoryName(String owner, String repositoryName);

    /**
     * Delete repository description by owner and repository name
     * 
     * @param owner Repository owner
     * @param repositoryName Repository name
     * @return number of deleted records
     */
    int deleteByOwnerAndRepositoryName(String owner, String repositoryName);

    /**
     * Find all repository descriptions by owner
     * 
     * @param owner Repository owner
     * @return List of RepositoryDescription
     */
    @Query("SELECT rd FROM RepositoryDescription rd WHERE rd.owner = :owner ORDER BY rd.updatedAt DESC")
    java.util.List<RepositoryDescription> findByOwnerOrderByUpdatedAtDesc(@Param("owner") String owner);

    /**
     * Count repository descriptions by owner
     * 
     * @param owner Repository owner
     * @return count of descriptions
     */
    long countByOwner(String owner);

    /**
     * Find repository descriptions by created by user
     * 
     * @param username Username who created the description
     * @return List of RepositoryDescription
     */
    @Query("SELECT rd FROM RepositoryDescription rd WHERE rd.createdBy = :username ORDER BY rd.createdAt DESC")
    java.util.List<RepositoryDescription> findByCreatedByOrderByCreatedAtDesc(@Param("username") String username);

    /**
     * Find repository descriptions by updated by user
     * 
     * @param username Username who last updated the description
     * @return List of RepositoryDescription
     */
    @Query("SELECT rd FROM RepositoryDescription rd WHERE rd.updatedBy = :username ORDER BY rd.updatedAt DESC")
    java.util.List<RepositoryDescription> findByUpdatedByOrderByUpdatedAtDesc(@Param("username") String username);
}
