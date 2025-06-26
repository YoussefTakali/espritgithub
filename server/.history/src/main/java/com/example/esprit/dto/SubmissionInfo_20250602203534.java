@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionInfo {
    private String submittedBy;           // Individual who clicked submit
    private String submissionType;        // "Individual", "Group", or "Class"
    private String studentId;            // For individual submissions
    private Long groupId;                // For group submissions
    private Long classId;                // For class submissions
    private String groupName;            // Name of the group
    private String className;            // Name of the class
    private boolean submittedOnBehalfOfGroup = false;
    private boolean submittedOnBehalfOfClass = false;
    
    @Builder.Default
    private Set<String> groupMembers = new HashSet<>();    // All members if group submission
    
    @Builder.Default
    private Set<String> classMembers = new HashSet<>();    // All members if class submission
}