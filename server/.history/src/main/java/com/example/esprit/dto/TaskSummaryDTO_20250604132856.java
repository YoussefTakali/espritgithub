@AllArgsConstructor
@Getter
public class TaskSummaryDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String status;
    private Long projectId;
}
