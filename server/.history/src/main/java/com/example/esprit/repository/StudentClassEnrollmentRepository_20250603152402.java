package com.example.esprit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.esprit.model.StudentClassEnrollment;


@Repository
public interface StudentClassEnrollmentRepository extends JpaRepository<StudentClassEnrollment, Long> {

}