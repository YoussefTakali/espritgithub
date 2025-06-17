package com.example.esprit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.esprit.dto.TaskSummaryDTO;
import com.example.esprit.model.Task;



@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

@Query("SELECT DISTINCT t FROM Task t " +
       "LEFT JOIN FETCH t.assignments " +
       "JOIN FETCH t.project " +  // Project is non-nullable (inner join)
       "WHERE t.createdBy = :teacherId")
List<Task> findByCreatedByWithAssignments(@Param("teacherId") String teacherId);
@Query("SELECT new com.example.esprit.dto.TaskSummaryDTO(t.id, t.title, t.dueDate, t.status, t.project.id) " +
       "FROM Task t WHERE t.project.group.id = :groupId AND t.isVisible = true")
List<TaskSummaryDTO> findVisibleTaskSummariesByGroupId(@Param("groupId") Long groupId);


}