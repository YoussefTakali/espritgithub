import { Component, OnInit } from '@angular/core';
import { ProjectserviceService } from 'src/app/services/projectservice.service';

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css']
})
export class ProjectsComponent implements OnInit {

  projects: any; 
  errorMessage: string = '';

  constructor(private projectService: ProjectserviceService) {}

  ngOnInit() {
    this.fetchProjects();
      console.log('Projects:', this.projects);
  }

  fetchProjects() {
    this.projectService.getProjectsByTeacher().subscribe({
      next: (projects) => {
        this.projects = projects;
      },
      error: (err) => {
        console.error('Error fetching projects', err);
        this.errorMessage = 'Failed to load projects.';
      }
    });
  }
}
