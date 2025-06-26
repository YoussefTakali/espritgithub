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
    List<Project> findByGroups_Member(Long classId);

}