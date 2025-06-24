package esprit.git_push_test.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple controller to test GitHub metadata functionality
 */
@RestController
@RequestMapping("/api/github/test")
public class GitHubMetadataTestController {

    @Autowired
    private GitHubMetadataController metadataController;


}
