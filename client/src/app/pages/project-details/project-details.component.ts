import { Component, type OnInit } from "@angular/core"
import { trigger, style, transition, animate } from "@angular/animations"
import { ProjectserviceService } from "src/app/services/projectservice.service"
import { ActivatedRoute } from "@angular/router"

@Component({
  selector: "app-project-details",
  templateUrl: "./project-details.component.html",
  styleUrls: ["./project-details.component.scss"],
  animations: [
    trigger("expandCollapse", [
      transition(":enter", [
        style({ height: "0", opacity: 0 }),
        animate("200ms ease-out", style({ height: "*", opacity: 1 })),
      ]),
      transition(":leave", [
        style({ height: "*", opacity: 1 }),
        animate("200ms ease-in", style({ height: "0", opacity: 0 })),
      ]),
    ]),
  ],
})
export class ProjectDetailsComponent implements OnInit {
  projectId!: number
  projectData: any

  expandedClasses: Set<number> = new Set()
  expandedGroups: Set<number> = new Set()

  constructor(
    private route: ActivatedRoute,
    private projectService: ProjectserviceService,
  ) {}

  ngOnInit(): void {
    this.projectId = Number(this.route.snapshot.paramMap.get("id"))
    this.projectService.getProjectById(this.projectId).subscribe({
      next: (data) => {
        this.projectData = data
      },
      error: (error) => {
        console.error("Error fetching project details:", error)
      },
    })
  }

  toggleClass(classId: number) {
    if (this.expandedClasses.has(classId)) {
      this.expandedClasses.delete(classId)
    } else {
      this.expandedClasses.add(classId)
    }
  }

  toggleGroup(groupId: number) {
    if (this.expandedGroups.has(groupId)) {
      this.expandedGroups.delete(groupId)
    } else {
      this.expandedGroups.add(groupId)
    }
  }

  // Placeholder: Add class button click handler
  addClass() {
    // You can implement logic later
    console.log("Add Class clicked")
  }

  // Placeholder: Add/edit group button handlers
  addGroup(classId: number) {
    console.log("Add Group clicked for class", classId)
  }

  editGroup(groupId: number) {
    console.log("Edit Group clicked for group", groupId)
  }

  // Placeholder: Assign tasks
  assignTaskToGroup(groupId: number) {
    console.log("Assign task to group", groupId)
  }

  assignTaskToStudent(studentId: number) {
    console.log("Assign task to student", studentId)
  }

  getGroupsForClass(classId: number): any[] {
    if (!this.projectData?.groups) return []
    return this.projectData.groups.filter((group: any) => group.classId === classId)
  }

  getStatusClass(): string {
    if (!this.projectData?.status) return "status-unknown"

    switch (this.projectData.status.toLowerCase()) {
      case "completed":
        return "status-completed"
      case "in progress":
        return "status-in-progress"
      case "pending":
        return "status-pending"
      case "overdue":
        return "status-overdue"
      default:
        return "status-unknown"
    }
  }
   showAddTaskModal = false;
  newTask = {
    title: '',
    description: '',
    dueDate: '',
    groupIdsInput: '',
  };

  // existing constructor and ngOnInit...

  openAddTaskModal() {
    this.showAddTaskModal = true;
    this.newTask = { title: '', description: '', dueDate: '', groupIdsInput: '' };
  }

  closeAddTaskModal() {
    this.showAddTaskModal = false;
  }

  submitAddTask() {
    if (!this.newTask.title || !this.newTask.dueDate) {
      return; // basic validation
    }

    // Parse group IDs input
    let groupIds: number[] = [];
    if (this.newTask.groupIdsInput.trim()) {
      groupIds = this.newTask.groupIdsInput
        .split(',')
        .map(id => parseInt(id.trim()))
        .filter(id => !isNaN(id));
    }

    // Prepare payload
    const payload = {
      title: this.newTask.title,
      description: this.newTask.description,
      dueDate: new Date(this.newTask.dueDate).toISOString(),
      createdBy: '61eb987b-d7ea-487d-b42d-9f9e4951af05', // replace with your logged user id or get dynamically
      projectId: this.projectId,
      scope: 'GROUP',  // because you want to assign to groups
      groupIds: groupIds,
    };

    console.log('Create task payload:', payload);

    // Call your service to create the task here
    // Assuming projectService has a method createTask:
    this.projectService.createTask(payload).subscribe({
      next: (task) => {
        console.log('Task created:', task);
        this.closeAddTaskModal();
        // optionally refresh project data or tasks list here
      },
      error: (error) => {
        console.error('Error creating task:', error);
      }
    });
  }
}
