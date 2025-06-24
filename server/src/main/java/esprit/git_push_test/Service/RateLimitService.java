package esprit.git_push_test.Service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitService {

    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LocalDateTime> lastResetTimes = new ConcurrentHashMap<>();

    // GitHub API rate limits
    private static final int GITHUB_HOURLY_LIMIT = 5000;
    private static final int GITHUB_MINUTE_LIMIT = 100; // Conservative estimate

    // Current GitHub rate limit status (updated from API responses)
    private volatile int remainingRequests = GITHUB_HOURLY_LIMIT;
    private volatile Instant rateLimitResetTime = Instant.now().plusSeconds(3600);

    public boolean canMakeRequest(String endpoint) {
        // Check if we have remaining requests according to GitHub
        if (remainingRequests <= 0) {
            if (Instant.now().isBefore(rateLimitResetTime)) {
                System.out.println("Rate limit exceeded, waiting until: " + rateLimitResetTime);
                return false;
            } else {
                // Reset time has passed, allow request
                remainingRequests = GITHUB_HOURLY_LIMIT;
            }
        }

        // Check our internal minute-based limiting
        String key = endpoint + "_minute";
        AtomicInteger minuteCount = requestCounts.computeIfAbsent(key, k -> new AtomicInteger(0));
        LocalDateTime lastReset = lastResetTimes.get(key);
        LocalDateTime now = LocalDateTime.now();

        // Reset minute counter if needed
        if (lastReset == null || now.isAfter(lastReset.plusMinutes(1))) {
            minuteCount.set(0);
            lastResetTimes.put(key, now);
        }

        // Check if we're under the minute limit
        if (minuteCount.get() >= GITHUB_MINUTE_LIMIT) {
            System.out.println("Minute rate limit exceeded for endpoint: " + endpoint);
            return false;
        }

        // Increment counters
        minuteCount.incrementAndGet();
        return true;
    }

    /**
     * Update rate limit status from GitHub API response headers
     */
    public void updateRateLimitFromHeaders(HttpHeaders headers) {
        try {
            String remaining = headers.getFirst("X-RateLimit-Remaining");
            String reset = headers.getFirst("X-RateLimit-Reset");

            if (remaining != null) {
                remainingRequests = Integer.parseInt(remaining);
                System.out.println("Updated remaining requests: " + remainingRequests);
            }

            if (reset != null) {
                long resetTimestamp = Long.parseLong(reset);
                rateLimitResetTime = Instant.ofEpochSecond(resetTimestamp);
                System.out.println("Rate limit resets at: " + rateLimitResetTime);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing rate limit headers: " + e.getMessage());
        }
    }

    /**
     * Check current GitHub rate limit status
     */
    public ResponseEntity<String> checkGitHubRateLimit(WebClient webClient, String githubToken) {
        try {
            return webClient.get()
                    .uri("/rate_limit")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .retrieve()
                    .toEntity(String.class)
                    .doOnNext(response -> {
                        // Update our internal rate limit status
                        updateRateLimitFromHeaders(response.getHeaders());
                    })
                    .block();
        } catch (WebClientResponseException e) {
            System.err.println("Error checking rate limit: " + e.getMessage());
            return null;
        }
    }

    public RateLimitStatus getRateLimitStatus() {
        RateLimitStatus status = new RateLimitStatus();
        status.remainingRequests = remainingRequests;
        status.resetTime = rateLimitResetTime;
        status.canMakeRequest = remainingRequests > 0 && Instant.now().isBefore(rateLimitResetTime);
        return status;
    }

    public void resetRateLimits() {
        requestCounts.clear();
        lastResetTimes.clear();
        remainingRequests = GITHUB_HOURLY_LIMIT;
        rateLimitResetTime = Instant.now().plusSeconds(3600);
        System.out.println("Rate limits manually reset");
    }

    public static class RateLimitStatus {
        public int remainingRequests;
        public Instant resetTime;
        public boolean canMakeRequest;

        // Keep existing fields for backward compatibility
        public int currentHourlyRequests = 0;
        public int maxHourlyRequests = GITHUB_HOURLY_LIMIT;
        public int currentMinuteRequests = 0;
        public int maxMinuteRequests = GITHUB_MINUTE_LIMIT;
        public LocalDateTime hourlyResetTime = LocalDateTime.now().plusHours(1);
        public LocalDateTime minuteResetTime = LocalDateTime.now().plusMinutes(1);

        public int getHourlyRequestsRemaining() {
            return Math.max(0, maxHourlyRequests - currentHourlyRequests);
        }

        public int getMinuteRequestsRemaining() {
            return Math.max(0, maxMinuteRequests - currentMinuteRequests);
        }

        public boolean canMakeRequest() {
            return canMakeRequest;
        }
    }}
