package esprit.git_push_test.Repository;

import esprit.git_push_test.Entity.PushedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PushedFilesRepository extends JpaRepository<PushedFile, Long> {

    public interface PushedFileRepository extends JpaRepository<PushedFile, Long> {
        List<PushedFile> findByRepoName(String repoName);
        List<PushedFile> findByRepoNameAndCommitId(String repoName, String commitId);
    }
}
