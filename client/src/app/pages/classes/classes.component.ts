import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

export interface Class {
  id: number;
  name: string;
  projects?: Project[];
  expanded?: boolean;   // add for UI expand toggle
}

export interface TaskAssignment {
  id: number;
  scope: 'GROUP' | 'CLASS' | 'STUDENT' | 'PROJECT';
  projectId?: number;
  groupId?: number;
  classId?: number;
  studentId?: string;
}

export interface Task {
  id: number;
  projectId: number;
  title: string;
  description: string;
  dueDate: string;
  createdBy: string;
  status: string;
  createdDate: string;
  assignments: TaskAssignment[];
  assignedTo?: string;  // for UI display
}

export interface Project {
  id: number;
  name: string;
  groups?: Group[];
  expanded?: boolean;   // add for UI expand toggle
}

export interface Group {
  id: number;
  name: string;
  memberIds: string[];
  expanded?: boolean;  // add for UI expand toggle
}

@Component({
  selector: 'app-classes',
  templateUrl: './classes.component.html',
  styleUrls: ['./classes.component.css']
})
export class ClassesComponent implements OnInit {
  classTree: Class[] = [];
  allTasks: Task[] = [];
  filteredTasks: Task[] = [];
  selectedProject?: Project;
  searchTerm: string = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    const teacherId = '61eb987b-d7ea-487d-b42d-9f9e4951af05';

    this.http.get<Class[]>(`http://localhost:8080/api/classes/by-teacher/${teacherId}`).subscribe({
      next: (data) => {
        this.classTree = data;
        // initialize expanded states as false
        this.classTree.forEach(c => c.expanded = false);
        this.classTree.forEach(c => c.projects?.forEach(p => p.expanded = false));
        this.classTree.forEach(c => c.projects?.forEach(p => p.groups?.forEach(g => g.expanded = false)));
      },
      error: (err) => console.error('Error loading class data', err)
    });

    this.http.get<Task[]>(`http://localhost:8080/api/tasks/by-teacher/${teacherId}`).subscribe({
      next: (tasks) => {
        this.allTasks = tasks;
        this.filteredTasks = tasks; // initially show all
      },
      error: (err) => console.error('Error loading task data', err)
    });
  }

  // Toggle class expand/collapse
  toggleClass(classe: Class) {
    classe.expanded = !classe.expanded;
  }

  // Toggle project expand/collapse
  toggleProject(project: Project) {
    project.expanded = !project.expanded;
  }

  // Toggle group expand/collapse
  toggleGroup(group: Group) {
    group.expanded = !group.expanded;
  }

  // Select project and filter tasks
  selectProject(project: Project) {
    this.selectedProject = project;
    this.filterByProject(project);
  }

  filterByStudent(studentId: string) {
    this.selectedProject = undefined;
    this.filteredTasks = this.allTasks.filter(task =>
      task.assignments.some(a => a.studentId === studentId)
    );
  }

  filterByGroup(group: Group) {
    this.selectedProject = undefined;
    const studentIds = group.memberIds;
    this.filteredTasks = this.allTasks.filter(task =>
      task.assignments.some(a => a.scope === 'STUDENT' && a.studentId && studentIds.includes(a.studentId))
      || task.assignments.some(a => a.scope === 'GROUP' && a.groupId === group.id)
    );
  }

  filterByProject(project: Project) {
    const groupIds = project.groups?.map(g => g.id) || [];
    const studentIds = project.groups?.flatMap(g => g.memberIds) || [];
    this.filteredTasks = this.allTasks.filter(task =>
      // assigned directly to project
      task.assignments.some(a => a.scope === 'PROJECT' && a.projectId === project.id)
      // OR assigned to any group in project
      || task.assignments.some(a => a.scope === 'GROUP' && a.groupId && groupIds.includes(a.groupId))
      // OR assigned to any student in groups of project
      || task.assignments.some(a => a.scope === 'STUDENT' && a.studentId && studentIds.includes(a.studentId))
    );
  }

  filterByClass(classe: Class) {
    this.selectedProject = undefined;
    const projectIds = classe.projects?.map(p => p.id) || [];
    const groupIds = classe.projects?.flatMap(p => p.groups?.map(g => g.id) || []) || [];
    const studentIds = classe.projects?.flatMap(p => p.groups?.flatMap(g => g.memberIds) || []) || [];

    this.filteredTasks = this.allTasks.filter(task =>
      task.assignments.some(a => a.scope === 'PROJECT' && a.projectId && projectIds.includes(a.projectId))
      || task.assignments.some(a => a.scope === 'GROUP' && a.groupId && groupIds.includes(a.groupId))
      || task.assignments.some(a => a.scope === 'STUDENT' && a.studentId && studentIds.includes(a.studentId))
      || task.assignments.some(a => a.scope === 'CLASS' && a.classId === classe.id)
    );
  }

  // For task add/edit/delete, you can keep your existing methods
  editTask(task: Task) { /* your code */ }
  deleteTask(task: Task) { /* your code */ }
    showAddTaskModal = false;
  newTask = {
    title: '',
    description: '',
    type: 'group',
    assignedGroups: [] as number[],
    dueDate: '',
    status: 'pending'
  };


  addTask() {
    this.showAddTaskModal = true;
  }

  closeModal() {
    this.showAddTaskModal = false;
    this.resetForm();
  }




availableGroups = [
  { id: 2, name: 'Group 2' },
  { id: 3, name: 'Group 3' },
  // Add more groups as needed
];
  onGroupCheckboxChange(event: Event, groupId: number) {
    const checkbox = event.target as HTMLInputElement;
    if (checkbox.checked) {
      if (!this.newTask.assignedGroups.includes(groupId)) {
        this.newTask.assignedGroups.push(groupId);
      }
    } else {
      this.newTask.assignedGroups = this.newTask.assignedGroups.filter(id => id !== groupId);
    }
  }
  private resetForm() {
    this.newTask = {
      title: '',
      description: '',
      type: 'group',
      assignedGroups: [],
      dueDate: '',
      status: 'pending'
    };
  }
  createTask() {
  const payload = {
    title: this.newTask.title,
    description: this.newTask.description,
    dueDate: this.newTask.dueDate + 'T23:59:00',  // Add time if needed
    createdBy: '61eb987b-d7ea-487d-b42d-9f9e4951af05',  // hardcoded for now
    projectId: 8,
    scope: 'GROUP',
    groupIds:[3]  // wrap in array
  };

  this.http.post('http://localhost:8080/api/tasks', payload).subscribe({
    next: (response) => {
      console.log('Task created successfully', response);
      this.closeModal();
      this.resetForm();
      // Optional: Reload task list
      this.ngOnInit();
    },
    error: (err) => {
      console.error('Error creating task', err);
    }
  });
}
}
