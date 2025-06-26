package com.example.esprit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.esprit.model.Task;



@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

@Query("SELECT DISTINCT t FROM Task t " +
       "LEFT JOIN FETCH t.assignments " +
       "JOIN FETCH t.project " +  // Project is non-nullable (inner join)
       "WHERE t.createdBy = :teacherId")
List<Task> findByCreatedByWithAssignments(@Param("teacherId") String teacherId);
@Query(value = """
    SELECT DISTINCT t.* FROM tasks t
    JOIN task_assignments a ON t.id = a.task_id
    JOIN groups g ON a.group_id = g.id
    JOIN group_members gm ON g.id = gm.group_id
    WHERE gm.student_id = :memberId
      AND t.is_visible = true
""", nativeQuery = true)
List<Task> findVisibleTasksByMemberIdInGroup(@Param("memberId") String memberId);

}