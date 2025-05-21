import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProjectserviceService } from 'src/app/services/projectservice.service';

@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrls: ['./project-details.component.css']
})
export class ProjectDetailsComponent implements OnInit {
  projectId!: number;
  projectData: any;

  expandedClasses: Set<number> = new Set();
  expandedGroups: Set<number> = new Set();

  constructor(
    private route: ActivatedRoute,
    private projectService: ProjectserviceService
  ) {}

  ngOnInit(): void {
    this.projectId = Number(this.route.snapshot.paramMap.get('id'));
    this.projectService.getProjectById(this.projectId).subscribe({
      next: (data) => {
        this.projectData = data;
      },
      error: (error) => {
        console.error('Error fetching project details:', error);
      }
    });
  }

  toggleClass(classId: number) {
    if (this.expandedClasses.has(classId)) {
      this.expandedClasses.delete(classId);
    } else {
      this.expandedClasses.add(classId);
    }
  }

  toggleGroup(groupId: number) {
    if (this.expandedGroups.has(groupId)) {
      this.expandedGroups.delete(groupId);
    } else {
      this.expandedGroups.add(groupId);
    }
  }

  // Placeholder: Add class button click handler
  addClass() {
    // You can implement logic later
    console.log('Add Class clicked');
  }

  // Placeholder: Add/edit group button handlers
  addGroup(classId: number) {
    console.log('Add Group clicked for class', classId);
  }

  editGroup(groupId: number) {
    console.log('Edit Group clicked for group', groupId);
  }

  // Placeholder: Assign tasks
  assignTaskToGroup(groupId: number) {
    console.log('Assign task to group', groupId);
  }

  assignTaskToStudent(studentId: number) {
    console.log('Assign task to student', studentId);
  }
}
