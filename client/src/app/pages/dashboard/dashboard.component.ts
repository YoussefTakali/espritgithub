import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivityComponent } from 'src/app/components/activity/activity.component';
import { GradingComponent } from 'src/app/components/grading/grading.component';
import { ProgressComponent } from 'src/app/components/progress/progress.component';
import { SubmissionsComponent } from 'src/app/components/submissions/submissions.component';
import { SidebarService } from 'src/app/services/sidebar.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats = [
    { title: 'Active Assignments', value: '8', description: '3 due this week', logo: 'assets/open-book.png' },
    { title: 'Student Submissions', value: '42', description: '12 need grading', logo: 'assets/git-commit-svgrepo-com.png' },
    { title: 'Class Average', value: '87%', description: 'Last 5 assignments', logo: 'assets/graduation-cap-svgrepo-com.png' },
    { title: 'Upcoming Deadlines', value: '5', description: 'Next 7 days', logo: 'assets/calendar-days-svgrepo-com.png' }
  ];
@ViewChild(ActivityComponent) activityComp!: ActivityComponent;
@ViewChild(SubmissionsComponent) submissionsComp!: SubmissionsComponent;
@ViewChild(ProgressComponent) progressComp!: ProgressComponent;
@ViewChild(GradingComponent) gradingComp!: GradingComponent;
onSearch() {
  switch (this.selectedTab) {
    // case 'activity':
    //   this.activityComp?.filter(this.searchQuery);
    //   break;
    // case 'submissions':
    //   this.submissionsComp?.filter(this.searchQuery);
    //   break;
    // case 'progress':
    //   this.progressComp?.filter(this.searchQuery);
    //   break;
    case 'grading':
      this.gradingComp?.filter(this.searchQuery);
      break;
  }
}

  currentTime: string = '';
  currentDate: string = '';
  aiAssited: boolean = false;
  sidebarVisible: boolean = false;
  selectedTab: string = 'activity'; // 👈 declared here

  constructor(private sidebarService: SidebarService) {}

  ngOnInit(): void {
    this.updateDateTime();
    setInterval(() => this.updateDateTime(), 1000);

    this.sidebarService.sidebarVisible$.subscribe((visible) => {
      this.sidebarVisible = visible;
      console.log('DashboardComponent: Sidebar visibility updated to', visible);
    });
  }

  updateDateTime(): void {
    const now = new Date();

    this.currentDate = now.toLocaleDateString(undefined, {
      weekday: 'short',
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });

    this.currentTime = now.toLocaleTimeString([], {
      hour: '2-digit',
      minute: '2-digit'
    });
  }
  searchQuery: string = '';

  changeTab(tab: string): void {
    this.selectedTab = tab;
  }

}
