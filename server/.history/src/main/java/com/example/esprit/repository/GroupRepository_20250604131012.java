package com.example.esprit.repository;

import com.example.esprit.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    
    // Find all groups belonging to a specific project
    List<Group> findByProjectId(Long projectId);

    // Find all groups belonging to a specific class
    List<Group> findByClasseId(Long classId);
        @Query("SELECT g FROM Group g JOIN g.memberIds m WHERE m = :memberId")
    List<Group> findByMemberId(@Param("memberId") String memberId);
}
