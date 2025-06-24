package esprit.git_push_test.Repository;

import esprit.git_push_test.Entity.PushedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PushedFilesRepository extends JpaRepository<PushedFile, Long> {

    List<PushedFile> findByRepoNameAndBranch(String repoName, String branch);

    @Query("SELECT DISTINCT p.repoName FROM PushedFile p WHERE p.pusherName = :pusherName")
    List<String> findDistinctRepoNamesByPusherName(@Param("pusherName") String pusherName);

    @Query("SELECT DISTINCT p.branch FROM PushedFile p WHERE p.repoName = :repoName AND p.pusherName = :pusherName")
    List<String> findDistinctBranchesByRepoNameAndPusherName(@Param("repoName") String repoName, @Param("pusherName") String pusherName);

    List<PushedFile> findByRepoNameAndBranchAndPusherName(String repoName, String branch, String pusherName);

    PushedFile findByRepoNameAndBranchAndPusherNameAndFilePath(String repoName, String branch, String pusherName, String filePath);

    @Query("SELECT DISTINCT p.content FROM PushedFile p WHERE p.pusherName = :pusherName AND p.branch = :branch")
    List<String> findDistinctContentByPusherNameAndBranch(
            @Param("pusherName") String pusherName,
            @Param("branch") String branch
    );
    @Query("SELECT DISTINCT p.fileExtension FROM PushedFile p WHERE p.pusherName = :pusherName")
    List<String> findFileExtensionsByPusherName(@Param("pusherName") String pusherName);


}