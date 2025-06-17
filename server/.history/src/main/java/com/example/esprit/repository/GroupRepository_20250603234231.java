package com.example.esprit.repository;

import com.example.esprit.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    
    // Find all groups belonging to a specific project
    List<Group> findByProjectId(Long projectId);

    // Find all groups belonging to a specific class
    List<Group> findByClasseId(Long classId);
}
