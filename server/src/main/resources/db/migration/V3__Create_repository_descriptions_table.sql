-- Create repository_descriptions table
CREATE TABLE repository_descriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner VARCHAR(100) NOT NULL,
    repository_name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    -- Unique constraint to ensure one description per repository
    UNIQUE KEY unique_owner_repo (owner, repository_name),
    
    -- Indexes for better performance
    INDEX idx_owner (owner),
    INDEX idx_repository_name (repository_name),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by),
    INDEX idx_created_at (created_at),
    INDEX idx_updated_at (updated_at)
);

-- Insert some sample data (optional)
INSERT INTO repository_descriptions (owner, repository_name, description, created_by, updated_by) VALUES
('salmabm', 'example-repo', 'This is a sample repository description for testing purposes.', 'salmabm', 'salmabm'),
('octocat', 'Hello-World', 'My first repository on GitHub! This is where I learn to code and collaborate.', 'octocat', 'octocat');
