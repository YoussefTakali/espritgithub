import jakarta.validation.constraints.NotNull;
import java.util.List;

public class GroupUpdateDTO {

    @NotNull(message = "Name must not be null")
    private String name;

    @NotNull(message = "Project ID must not be null")
    private Long projectId;

    @NotNull(message = "Class ID must not be null")
    private Long classId;

    private List<Long> memberIds;

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public List<Long> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
    }
}
