package com.example.esprit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.esprit.model.TeacherClassAssignment;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherClassAssignmentRepository extends JpaRepository<TeacherClassAssignment, Long> {
    // List<TeacherClassAssignment> findByTeacherId(String teacherId);
    // List<TeacherClassAssignment> findByTeachingClassId(Long classId);
    // Optional<TeacherClassAssignment> findByTeacherIdAndTeachingClassId(String teacherId, Long classId);
    // void deleteByTeacherIdAndTeachingClassId(String teacherId, Long classId);
}