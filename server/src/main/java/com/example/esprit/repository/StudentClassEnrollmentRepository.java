package com.example.esprit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.esprit.model.StudentClassEnrollment;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentClassEnrollmentRepository extends JpaRepository<StudentClassEnrollment, Long> {
    // List<StudentClassEnrollment> findByEnrolledClassId(Long classId);
    // Optional<StudentClassEnrollment> findByStudentId(String studentId);
    // void deleteByStudentId(String studentId);
}