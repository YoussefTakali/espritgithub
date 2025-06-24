package esprit.git_push_test.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to handle GitHub events and trigger webhook creation for new repositories
 */
@Service
public class GitHubEventListener {

    private final WebhookService webhookService;
    private final ObjectMapper objectMapper;

    @Autowired
    public GitHubEventListener(WebhookService webhookService) {
        this.webhookService = webhookService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Process a GitHub webhook event
     *
     * @param payload The webhook payload
     * @param event The event type
     * @return true if the event was processed successfully, false otherwise
     */
    public boolean processEvent(String payload, String event) {
        try {
            System.out.println("Processing GitHub event: " + event);

            // Parse the payload
            JsonNode rootNode = objectMapper.readTree(payload);

            // Handle different event types
            switch (event) {
                case "repository":
                    return handleRepositoryEvent(rootNode);
                case "push":
                    return handlePushEvent(rootNode);
                case "ping":
                    System.out.println("Received ping event - webhook is working!");
                    return true;
                case "organization":
                    return handleOrganizationEvent(rootNode);
                default:
                    System.out.println("Unhandled event type: " + event);
                    return false;
            }
        } catch (Exception e) {
            System.err.println("Error processing GitHub event: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Handle a repository event (created, deleted, etc.)
     */
    private boolean handleRepositoryEvent(JsonNode rootNode) {
        try {
            // Get the action (created, deleted, etc.)
            String action = rootNode.path("action").asText();

            // Get repository information
            JsonNode repoNode = rootNode.path("repository");
            String repoName = repoNode.path("name").asText();
            String ownerLogin = repoNode.path("owner").path("login").asText();
            boolean isPrivate = repoNode.path("private").asBoolean();

            System.out.println("Repository event: " + action + " for " + ownerLogin + "/" + repoName);

            // If a new repository is created and it's public, create a webhook for it
            if ("created".equals(action) && !isPrivate) {
                System.out.println("New public repository created: " + ownerLogin + "/" + repoName);
                return webhookService.checkAndCreateWebhookForRepo(ownerLogin, repoName);
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error handling repository event: " + e.getMessage());
            return false;
        }
    }

    /**
     * Handle a push event
     */
    private boolean handlePushEvent(JsonNode rootNode) {
        try {
            // Get repository information
            JsonNode repoNode = rootNode.path("repository");
            String repoName = repoNode.path("name").asText();
            String ownerLogin = repoNode.path("owner").path("login").asText();
            boolean isPrivate = repoNode.path("private").asBoolean();

            // If this is a push to a public repository, make sure it has a webhook
            if (!isPrivate) {
                // Check if the repository has a webhook, create one if it doesn't
                return webhookService.checkAndCreateWebhookForRepo(ownerLogin, repoName);
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error handling push event: " + e.getMessage());
            return false;
        }
    }

    /**
     * Handle an organization event (repository created, deleted, etc.)
     */
    private boolean handleOrganizationEvent(JsonNode rootNode) {
        try {
            // Get the action (created, deleted, etc.)
            String action = rootNode.path("action").asText();

            // If this is a repository_created event
            if ("repository_created".equals(action)) {
                // Get repository information
                JsonNode repoNode = rootNode.path("repository");
                String repoName = repoNode.path("name").asText();
                String ownerLogin = rootNode.path("organization").path("login").asText();
                boolean isPrivate = repoNode.path("private").asBoolean();

                System.out.println("Organization event: repository_created for " + ownerLogin + "/" + repoName);

                // If the repository is public, create a webhook for it
                if (!isPrivate) {
                    System.out.println("New public repository created in organization: " + ownerLogin + "/" + repoName);
                    return webhookService.checkAndCreateWebhookForRepo(ownerLogin, repoName);
                }
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error handling organization event: " + e.getMessage());
            return false;
        }
    }
}
