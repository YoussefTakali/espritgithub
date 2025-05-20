import { Component, OnInit } from '@angular/core';
import { ProjectserviceService } from 'src/app/services/projectservice.service';

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css']
})
export class ProjectsComponent implements OnInit {

  projects: any[] = [];
  filteredProjects: any[] = [];
  errorMessage: string = '';

  searchTerm: string = '';
  statusFilter: string = '';

  constructor(private projectService: ProjectserviceService) {}

  ngOnInit() {
    this.fetchProjects();
  }

  fetchProjects() {
    this.projectService.getProjectsByTeacher().subscribe({
      next: (backendProjects) => {
        this.projects = backendProjects.map((proj: any) => ({
          title: proj.name,
          subtitle: proj.associatedClass?.name || 'No class',
          date: new Date(proj.createdDate).toLocaleDateString(),
          progress: 50, // Replace with actual progress if available
          remainingDays: this.getRemainingDays(proj.dueDate),
          status: proj.status || 'inProgress', // Assume a default if not provided
          users: [
            'https://i.pravatar.cc/30?img=1',
            'https://i.pravatar.cc/30?img=2'
          ]
        }));
        this.applyFilters(); // Apply filters after loading
      },
      error: (err) => {
        console.error('Error fetching projects', err);
        this.errorMessage = 'Failed to load projects.';
      }
    });
  }

  applyFilters() {
    this.filteredProjects = this.projects.filter(project => {
      const matchesSearch =
        project.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        project.subtitle.toLowerCase().includes(this.searchTerm.toLowerCase());

      const matchesStatus =
        this.statusFilter ? project.status === this.statusFilter : true;

      return matchesSearch && matchesStatus;
    });
  }

  getRemainingDays(targetDate: string): number {
    const now = new Date();
    const target = new Date(targetDate);
    const diffInMs = target.getTime() - now.getTime();
    const diffInDays = Math.ceil(diffInMs / (1000 * 60 * 60 * 24));
    return Math.max(0, diffInDays);
  }
  showAddModal: boolean = false;

newProject: any = {
  name: '',
  description: '',
  dueDate: '',
  dueTime:'',
  status: 'ACTIVE',
  createdBy: '61eb987b-d7ea-487d-b42d-9f9e4951af05',
  associatedClass: {
    id: 1
  }
};

classOptions: any[] = []; // Replace with actual classes from backend

openAddProjectModal() {
  this.showAddModal = true;
}

closeAddProjectModal() {
  this.showAddModal = false;
  this.resetForm();
}

resetForm() {
  this.newProject = {
    name: '',
    description: '',
    dueDate: '',
    dueTime:'',
    status: 'ACTIVE',
    createdBy: '61eb987b-d7ea-487d-b42d-9f9e4951af05',
    associatedClass: {
      id: 1
    }
  };
}

submitNewProject() {
  const createdById = localStorage.getItem('id') || '';

  // Combine date + time (e.g. '2025-05-21' + '14:30') into ISO datetime string
  const combinedDateTime = this.combineDateAndTime(this.newProject.dueDate, this.newProject.dueTime);

  const projectPayload = {
    name: this.newProject.name,
    description: this.newProject.description,
    createdDate: new Date().toISOString(),   // current datetime
    dueDate: combinedDateTime,                // combined ISO string
    status: this.newProject.status,
    createdBy: createdById,
    associatedClass: {
      id: 1
    }
  };

  this.projectService.createProject(projectPayload).subscribe({
    next: (response) => {
      console.log('Project created:', response);
      this.closeAddProjectModal();
      this.fetchProjects();
    },
    error: (error) => {
      console.error('Create project error:', error);
    }
  });
}

combineDateAndTime(date: string, time: string): string {
  // date: "2025-05-21", time: "14:30"
  // Return ISO string "2025-05-21T14:30:00"
  return `${date}T${time}:00`;
}}