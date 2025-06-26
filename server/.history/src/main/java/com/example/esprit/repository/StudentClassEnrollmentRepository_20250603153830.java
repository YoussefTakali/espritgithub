package com.example.esprit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.esprit.model.StudentClassEnrollment;


@Repository
public interface StudentClassEnrollmentRepository extends JpaRepository<StudentClassEnrollment, Long> {
    Optional<StudentClassEnrollment> findByStudentId(String studentId);
    void deleteByStudentId(String studentId);
}