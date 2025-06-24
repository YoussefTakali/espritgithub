package esprit.git_push_test.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class WebhookService {

    @Value("${github.token}")
    private String githubToken;

    @Value("${webhook.url}")
    private String webhookUrl;

    @Value("${github.webhook.secret:}")
    private String secret;

    private final RestTemplate restTemplate;

    // Keep track of repositories we've already processed
    private final Set<String> processedRepos = new HashSet<>();

    public WebhookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Scheduled task that runs every 5 minutes to check for repositories without webhooks
     * and create them automatically. Using a longer interval to avoid hitting API rate limits.
     */
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void checkAndCreateWebhooks() {
        // Only print log message if debug logging is enabled
        boolean verbose = false;

        if (verbose) {
            System.out.println("Starting scheduled webhook check...");
        }

        try {
            // Get all repositories
            List<Map<String, Object>> allRepos = getAllRepositories();

            if (allRepos.isEmpty()) {
                if (verbose) {
                    System.out.println("No repositories found. Check your GitHub token permissions.");
                }
                return;
            }

            if (verbose) {
                System.out.println("Found " + allRepos.size() + " repositories. Checking for new repositories...");
            }

            int createdCount = 0;
            int existingCount = 0;
            int errorCount = 0;
            int skippedPrivateCount = 0;
            int skippedProcessedCount = 0;

            // For each repository, check if it has our webhook
            for (Map<String, Object> repo : allRepos) {
                String repoName = (String) repo.get("name");
                Map<String, Object> ownerInfo = (Map<String, Object>) repo.get("owner");
                String ownerLogin = (String) ownerInfo.get("login");
                boolean isPrivate = Boolean.TRUE.equals(repo.get("private"));
                String repoKey = ownerLogin + "/" + repoName;

                try {
                    // Skip private repositories
                    if (isPrivate) {
                        if (verbose) {
                            System.out.println("Skipping private repository: " + repoKey);
                        }
                        skippedPrivateCount++;
                        continue;
                    }

                    // Skip repositories we've already processed successfully
                    if (processedRepos.contains(repoKey)) {
                        if (verbose) {
                            System.out.println("Skipping already processed repository: " + repoKey);
                        }
                        skippedProcessedCount++;
                        continue;
                    }

                    // Check if webhook exists
                    if (!webhookExists(ownerLogin, repoName)) {
                        // Create webhook
                        boolean created = createWebhook(ownerLogin, repoName);
                        if (created) {
                            createdCount++;
                            // Always log webhook creation
                            System.out.println("Created webhook for " + repoKey);
                            // Add to processed repos so we don't check it again
                            processedRepos.add(repoKey);
                        } else {
                            errorCount++;
                            System.out.println("Failed to create webhook for " + repoKey);
                        }
                    } else {
                        existingCount++;
                        if (verbose) {
                            System.out.println("Webhook already exists for " + repoKey);
                        }
                        // Add to processed repos so we don't check it again
                        processedRepos.add(repoKey);
                    }
                } catch (Exception e) {
                    errorCount++;
                    System.err.println("Error processing " + repoKey + ": " + e.getMessage());
                }
            }

            // Only log if something interesting happened or in verbose mode
            if (createdCount > 0 || errorCount > 0 || verbose) {
                System.out.println("Webhook check completed: " +
                        createdCount + " created, " +
                        existingCount + " already exist, " +
                        skippedPrivateCount + " private repos skipped, " +
                        skippedProcessedCount + " previously processed repos skipped, " +
                        errorCount + " errors");

                System.out.println("Total repositories tracked: " + processedRepos.size());
            }

        } catch (Exception e) {
            System.err.println("Error in scheduled webhook check: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Track the last time we successfully fetched repositories
    private long lastSuccessfulFetch = 0;
    private List<Map<String, Object>> cachedRepos = new ArrayList<>();

    /**
     * Get all repositories that the GitHub token has access to
     * This method uses caching to avoid hitting API rate limits
     */
    private List<Map<String, Object>> getAllRepositories() {
        // If we've fetched repositories in the last hour, use the cached results
        long currentTime = System.currentTimeMillis();
        long oneHourInMillis = 60 * 60 * 1000;

        if (!cachedRepos.isEmpty() && (currentTime - lastSuccessfulFetch < oneHourInMillis)) {
            System.out.println("Using cached repository list to avoid API rate limits");
            return cachedRepos;
        }

        List<Map<String, Object>> allRepos = new ArrayList<>();

        try {
            // Set up HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(githubToken);
            headers.set("Accept", "application/vnd.github+json");
            headers.set("X-GitHub-Api-Version", "2022-11-28");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // API endpoint for user's repositories
            String apiEndpoint = "https://api.github.com/user/repos";

            // Handle pagination
            int page = 1;
            boolean hasMorePages = true;

            while (hasMorePages) {
                String pageUrl = apiEndpoint + "?page=" + page + "&per_page=100";

                try {
                    ResponseEntity<List<Map<String, Object>>> pageResponse = restTemplate.exchange(
                            pageUrl,
                            HttpMethod.GET,
                            entity,
                            new ParameterizedTypeReference<List<Map<String, Object>>>() {});

                    List<Map<String, Object>> pageRepos = pageResponse.getBody();

                    if (pageRepos != null && !pageRepos.isEmpty()) {
                        allRepos.addAll(pageRepos);
                        page++;
                    } else {
                        hasMorePages = false;
                    }
                } catch (Exception e) {
                    System.err.println("Error fetching repositories page " + page + ": " + e.getMessage());

                    // If we hit a rate limit, return cached repos if available
                    if (e.getMessage() != null && e.getMessage().contains("rate limit")) {
                        System.out.println("Hit API rate limit, using cached repository list");
                        return cachedRepos;
                    }

                    hasMorePages = false;
                }
            }

            // If we successfully fetched repositories, update the cache
            if (!allRepos.isEmpty()) {
                cachedRepos = allRepos;
                lastSuccessfulFetch = currentTime;
            }
        } catch (Exception e) {
            System.err.println("Error fetching repositories: " + e.getMessage());
            // Return cached repos on error
            return cachedRepos;
        }

        return allRepos;
    }

    // Cache for webhook existence checks to avoid API rate limits
    private final Map<String, Boolean> webhookExistsCache = new HashMap<>();
    private long lastWebhookCacheClear = 0;

    /**
     * Check if a working webhook already exists for the repository
     * This method uses caching to avoid hitting API rate limits
     */
    private boolean webhookExists(String owner, String repo) {
        String repoKey = owner + "/" + repo;

        // Clear cache every hour
        long currentTime = System.currentTimeMillis();
        long oneHourInMillis = 60 * 60 * 1000;

        if (currentTime - lastWebhookCacheClear > oneHourInMillis) {
            webhookExistsCache.clear();
            lastWebhookCacheClear = currentTime;
        }

        // Check cache first
        if (webhookExistsCache.containsKey(repoKey)) {
            return webhookExistsCache.get(repoKey);
        }

        // Set up HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // API endpoint for repository webhooks
        String hooksUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/hooks";

        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    hooksUrl,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {});

            List<Map<String, Object>> hooks = response.getBody();

            if (hooks != null) {
                // Check if any of the hooks point to our webhook URL
                for (Map<String, Object> hook : hooks) {
                    Map<String, Object> config = (Map<String, Object>) hook.get("config");
                    if (config != null && webhookUrl.equals(config.get("url"))) {
                        // Check if the webhook is active
                        Boolean active = (Boolean) hook.get("active");

                        // Check if there are recent delivery failures
                        String hookId = hook.get("id").toString();
                        if (active != null && active) {
                            // If the webhook is active, consider it working
                            webhookExistsCache.put(repoKey, true);
                            return true;
                        } else {
                            // If the webhook is inactive, delete it so we can recreate it
                            System.out.println("Found inactive webhook for " + owner + "/" + repo + ". Will recreate it.");
                            deleteWebhook(owner, repo, hookId);
                            webhookExistsCache.put(repoKey, false);
                            return false;
                        }
                    }
                }
            }

            webhookExistsCache.put(repoKey, false);
            return false;
        } catch (HttpClientErrorException e) {
            // If we get a 404, the repository doesn't have any webhooks
            if (e.getStatusCode().value() == 404) {
                webhookExistsCache.put(repoKey, false);
                return false;
            }

            // If we hit a rate limit, assume webhook doesn't exist but don't cache the result
            if (e.getStatusCode().value() == 403 && e.getMessage() != null && e.getMessage().contains("rate limit")) {
                System.out.println("Hit API rate limit when checking webhook for " + repoKey);
                return false;
            }

            // For other errors, log and assume webhook doesn't exist
            System.err.println("Error checking webhooks for " + owner + "/" + repo + ": " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error checking webhooks for " + owner + "/" + repo + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a webhook
     */
    private void deleteWebhook(String owner, String repo, String hookId) {
        // Set up HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // API endpoint for deleting a webhook
        String deleteUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/hooks/" + hookId;

        try {
            restTemplate.exchange(deleteUrl, HttpMethod.DELETE, entity, Void.class);
            System.out.println("Deleted inactive webhook for " + owner + "/" + repo);
        } catch (Exception e) {
            System.err.println("Error deleting webhook for " + owner + "/" + repo + ": " + e.getMessage());
        }
    }

    /**
     * Check and create webhook for a specific repository
     *
     * @param owner The owner of the repository
     * @param repo The name of the repository
     * @return true if the webhook was created or already exists, false otherwise
     */
    public boolean checkAndCreateWebhookForRepo(String owner, String repo) {
        try {
            String repoKey = owner + "/" + repo;

            // Skip if we've already processed this repository successfully
            if (processedRepos.contains(repoKey)) {
                return true;
            }

            // Check if webhook exists
            if (!webhookExists(owner, repo)) {
                // Create webhook
                boolean created = createWebhook(owner, repo);
                if (created) {
                    System.out.println("Created webhook for " + repoKey);
                    // Add to processed repos
                    processedRepos.add(repoKey);
                    return true;
                } else {
                    System.out.println("Failed to create webhook for " + repoKey);
                    return false;
                }
            } else {
                // Add to processed repos
                processedRepos.add(repoKey);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error processing " + owner + "/" + repo + ": " + e.getMessage());
            return false;
        }
    }

    // Track repositories where webhook creation failed due to rate limits
    private final Set<String> rateLimitedRepos = new HashSet<>();

    private boolean createWebhook(String owner, String repo) {
        String repoKey = owner + "/" + repo;

        // If we've hit rate limits for this repo recently, skip it
        if (rateLimitedRepos.contains(repoKey)) {
            System.out.println("Skipping webhook creation for " + repoKey + " due to previous rate limit");
            return false;
        }

        // First, verify the repository exists and we have access to it
        String repoUrl = String.format("https://api.github.com/repos/%s/%s", owner, repo);
        HttpHeaders verifyHeaders = new HttpHeaders();
        verifyHeaders.setBearerAuth(githubToken);
        verifyHeaders.set("Accept", "application/vnd.github+json");
        HttpEntity<Void> verifyEntity = new HttpEntity<>(verifyHeaders);

        try {
            ResponseEntity<Map> repoResponse = restTemplate.exchange(
                repoUrl, HttpMethod.GET, verifyEntity, Map.class);

            // Check if we have admin access to this repository
            if (repoResponse.getBody() != null) {
                Boolean hasAdminPermission = false;
                if (repoResponse.getBody().containsKey("permissions")) {
                    Map<String, Boolean> permissions = (Map<String, Boolean>) repoResponse.getBody().get("permissions");
                    hasAdminPermission = permissions.getOrDefault("admin", false);
                }

                if (!hasAdminPermission) {
                    System.out.println("Cannot create webhook for " + repoKey + ": No admin permission");
                    return false;
                }
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                System.out.println("Repository not found: " + repoKey);
                return false;
            } else if (e.getStatusCode().value() == 403) {
                System.out.println("No permission to access repository: " + repoKey);
                return false;
            } else {
                System.out.println("Error verifying repository " + repoKey + ": " + e.getMessage());
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error verifying repository " + repoKey + ": " + e.getMessage());
            return false;
        }

        // Set up HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");

        // Configure the webhook
        Map<String, Object> config = new HashMap<>();
        config.put("url", webhookUrl);
        config.put("content_type", "json");
        if (secret != null && !secret.isEmpty()) {
            config.put("secret", secret);
        }

        // Define the events to listen for
        List<String> events = Arrays.asList("push", "pull_request");

        Map<String, Object> body = new HashMap<>();
        body.put("name", "web");
        body.put("active", true);
        body.put("events", events);
        body.put("config", config);

        HttpEntity<Map<String, Object>> hookEntity = new HttpEntity<>(body, headers);

        // API endpoint for creating repository webhooks
        String hooksUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/hooks";

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(hooksUrl, hookEntity, Map.class);
            System.out.println("Successfully created webhook for " + repoKey);

            // Successfully created webhook, remove from rate limited repos if it was there
            rateLimitedRepos.remove(repoKey);

            // Update webhook cache
            webhookExistsCache.put(repoKey, true);

            return true;
        } catch (HttpClientErrorException e) {
            // If we hit a rate limit, track this repo
            if (e.getStatusCode().value() == 403 && e.getMessage() != null && e.getMessage().contains("rate limit")) {
                System.out.println("Hit API rate limit when creating webhook for " + repoKey);
                rateLimitedRepos.add(repoKey);
            } else if (e.getStatusCode().value() == 404) {
                System.out.println("Cannot create webhook for " + repoKey + ": Repository not found or no admin access");
            } else if (e.getStatusCode().value() == 422) {
                System.out.println("Webhook already exists or validation failed for " + repoKey);
            }

            System.err.println("Error creating webhook for " + owner + "/" + repo + ": " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Error creating webhook for " + owner + "/" + repo + ": " + e.getMessage());
            return false;
        }
    }
}
