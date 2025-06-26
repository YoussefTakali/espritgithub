package com.example.esprit.repository;

import java.time.LocalDate;
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
@Query("""
    SELECT DISTINCT t FROM Task t 
    JOIN FETCH t.assignments ta 
    LEFT JOIN FETCH ta.group g 
    WHERE t.isVisible = true 
    AND (
        (ta.scope = 'GROUP' AND :memberId MEMBER OF g.memberIds) OR 
        (ta.scope = 'INDIVIDUAL' AND ta.studentId = :memberId) OR 
        (ta.scope = 'CLASS')
    )
    ORDER BY t.createdDate DESC
""")
List<Task> findVisibleTasksForMember(@Param("memberId") String memberId);
   @Query("""
        SELECT
            COUNT(CASE WHEN ta.status = 'PENDING' THEN 1 END),
            COUNT(CASE WHEN ta.status = 'PENDING' AND ta.endDate BETWEEN CURRENT_DATE AND :endOfWeek THEN 1 END)
        FROM TaskAssignment ta
        WHERE ta.task.createdBy.id = :teacherId
    """)
    Object[] countPendingAndEndingThisWeekByTeacher(@Param("teacherId") Long teacherId,
                                                   @Param("endOfWeek") LocalDate endOfWeek);
} 