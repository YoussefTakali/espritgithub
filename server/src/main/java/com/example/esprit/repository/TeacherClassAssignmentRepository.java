package com.example.esprit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.esprit.model.TeacherClassAssignment;


@Repository
public interface TeacherClassAssignmentRepository extends JpaRepository<TeacherClassAssignment, Long> {

}