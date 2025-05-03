import { Component,OnInit } from '@angular/core';

@Component({
  selector: 'app-app-professor-dashboard',
  templateUrl: './app-professor-dashboard.component.html',
  styleUrls: ['./app-professor-dashboard.component.css']
})
export class AppProfessorDashboardComponent  implements OnInit {
  professorName = 'Dr. Youssef Takali';
  today = new Date();


  collapsed = false; // 👈 fix for [class.collapsed]
  showCreateRepo = true; // 👈 fix for *ngIf
  // Mock values (can be fetched from a service)
  projectCount = 5;
  groupCount = 12;
  repoCount = 8;
  branchCount = 14;

  selectedProjectId: number=0;
  selectedGroupId: number=0;

  projects = [
    { id: 1, name: 'AI Chatbot Project' },
    { id: 2, name: 'Web3 Voting System' }
  ];

  groups = [
    { id: 101, name: 'Group A' },
    { id: 102, name: 'Group B' }
  ];

  ngOnInit(): void {}

  toggleSidebar() {
    this.collapsed = !this.collapsed;
  }

  createRepos() {
    console.log('Creating repo for:', this.selectedProjectId, this.selectedGroupId);
    // 🔁 Replace this with actual API call if needed
  }
}



