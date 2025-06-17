package com.example.esprit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.esprit.model.Project;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.associatedClasses WHERE p.createdBy = :teacherId")
    List<Project> findByCreatedBy(@Param("teacherId") String teacherId);
  
    // Change this method to reflect the new many-to-many relationship
    List<Project> findByAssociatedClassesId(Long classId);
@Query(value = """
    SELECT DISTINCT p.*
    FROM project p
    JOIN group_table g ON g.project_id = p.id
    JOIN group_members_id gm ON gm.group_id = g.id
    WHERE gm.members_id = :memberId
""", nativeQuery = true)
List<Project> findProjectsByMemberId(@Param("memberId") Long memberId);

}