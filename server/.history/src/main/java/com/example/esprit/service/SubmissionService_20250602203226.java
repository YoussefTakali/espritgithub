package com.example.esprit.service;

import com.example.esprit.model.Submission;
import com.example.esprit.model.Task;

public class SubmissionService {
    
    public SubmissionInfo analyzeSubmission(Submission submission) {
        Task task = submission.getTask();
        String submittedBy = submission.getSubmittedBy();
        
        SubmissionInfo info = new SubmissionInfo();
        info.setSubmittedBy(submittedBy);
        
        // Check task assignments to determine scope
        for (TaskAssignment assignment : task.getAssignments()) {
            switch (assignment.getScope()) {
                case INDIVIDUAL:
                    info.setSubmissionType("Individual");
                    info.setStudentId(submittedBy);
                    break;
                    
                case GROUP:
                    info.setSubmissionType("Group");
                    info.setGroupId(assignment.getGroup().getId());
                    info.setSubmittedOnBehalfOfGroup(true);
                    // Get all group members
                    info.setGroupMembers(getGroupMembers(assignment.getGroup().getId()));
                    break;
                    
                case CLASS:
                    info.setSubmissionType("Class");
                    info.setClassId(assignment.getClasse().getId());
                    info.setSubmittedOnBehalfOfClass(true);
                    // Get all class members
                    info.setClassMembers(getClassMembers(assignment.getClasse().getId()));
                    break;
            }
        }
        
        return info;
    }
    
    private Set<String> getGroupMembers(Long groupId) {
        // Query group_members table
        return groupMemberRepository.findByGroupId(groupId)
            .stream()
            .map(GroupMember::getStudentId)
            .collect(Collectors.toSet());
    }
    
    private Set<String> getClassMembers(Long classId) {
        // Query student_class_enrollments table
        return studentClassEnrollmentRepository.findByClassId(classId)
            .stream()
            .map(StudentClassEnrollment::getStudentId)
            .collect(Collectors.toSet());
    }
}

@Data
public class SubmissionInfo {
    private String submittedBy;           // Individual who clicked submit
    private String submissionType;        // "Individual", "Group", or "Class"
    private String studentId;            // For individual submissions
    private Long groupId;                // For group submissions
    private Long classId;                // For class submissions
    private boolean submittedOnBehalfOfGroup = false;
    private boolean submittedOnBehalfOfClass = false;
    private Set<String> groupMembers;    // All members if group submission
    private Set<String> classMembers;    // All members if class submission
}