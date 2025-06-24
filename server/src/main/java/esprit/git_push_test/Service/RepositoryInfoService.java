package esprit.git_push_test.Service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to manage GitHub repository information
 * This service maintains repository information that can be populated from webhook data
 * or manually configured by users.
 */
@Service
public class RepositoryInfoService {

    // Using ConcurrentHashMap for thread safety
    private final ConcurrentHashMap<String, String> repoInfo = new ConcurrentHashMap<>();

    /**
     * Updates repository information from webhook payload
     */
    public void updateFromWebhook(String owner, String repo, String branch, String pusherName) {
        setRepoOwner(owner);
        setRepoName(repo);
        setBranch(branch);
        setPusherName(pusherName);
    }

    public String getRepoOwner() {
        return repoInfo.get("owner");
    }

    public void setRepoOwner(String owner) {
        repoInfo.put("owner", owner);
    }

    public String getRepoName() {
        return repoInfo.get("repo");
    }

    public void setRepoName(String repo) {
        repoInfo.put("repo", repo);
    }

    public String getBranch() {
        return repoInfo.getOrDefault("branch", "main");
    }

    public void setBranch(String branch) {
        repoInfo.put("branch", branch);
    }

    public String getPusherName() {
        return repoInfo.get("pusherName");
    }

    public void setPusherName(String pusherName) {
        repoInfo.put("pusherName", pusherName);
    }

    /**
     * Checks if repository information is complete
     */
    public boolean isRepositoryConfigured() {
        return getRepoOwner() != null && getRepoName() != null;
    }
}