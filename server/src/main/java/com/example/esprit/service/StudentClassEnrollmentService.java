// package com.example.esprit.service;

// import com.example.esprit.model.Class;
// import com.example.esprit.model.EnrollmentStatus;
// import com.example.esprit.model.StudentClassEnrollment;
// import com.example.esprit.repository.StudentClassEnrollmentRepository;
// import com.example.esprit.repository.ClassRepository;

// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// @Service
// @RequiredArgsConstructor
// public class StudentClassEnrollmentService {

//     private final StudentClassEnrollmentRepository enrollmentRepository;
//     private final ClassRepository classRepository;

//     public StudentClassEnrollment enrollStudentToClass(String studentId, Long classId) {
//         Optional<Class> optClass = classRepository.findById(classId);
//         if (optClass.isEmpty()) {
//             throw new IllegalArgumentException("Class with id " + classId + " not found");
//         }

//         // Check if student is already enrolled
//         Optional<StudentClassEnrollment> existing = enrollmentRepository.findByStudentId(studentId);
//         if (existing.isPresent()) {
//             throw new IllegalArgumentException("Student " + studentId + " already enrolled");
//         }

//         StudentClassEnrollment enrollment = StudentClassEnrollment.builder()
//                 .studentId(studentId)
//                 .enrolledClass(optClass.get())
//                 .enrollmentDate(LocalDateTime.now())
//                 .status(EnrollmentStatus.ACTIVE)
//                 .build();

//         return enrollmentRepository.save(enrollment);
//     }

//     public List<StudentClassEnrollment> getStudentsByClassId(Long classId) {
//         return enrollmentRepository.findByEnrolledClassId(classId);
//     }

//     public void removeStudentEnrollment(String studentId) {
//         enrollmentRepository.deleteByStudentId(studentId);
//     }
// }
