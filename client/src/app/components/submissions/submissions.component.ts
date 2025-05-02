import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { SidebarService } from 'src/app/services/sidebar.service';

@Component({
  selector: 'app-submissions',
  templateUrl: './submissions.component.html',
  styleUrls: ['./submissions.component.css']
})
export class SubmissionsComponent implements OnInit, OnDestroy {
  sidebarVisible: boolean = false;
  private sidebarSub!: Subscription;

  constructor(private sidebarService: SidebarService) {}

  ngOnInit(): void {
    this.sidebarSub = this.sidebarService.sidebarVisible$.subscribe((visible:boolean) => {
      this.sidebarVisible = visible;
      console.log('Sidebar visibility in SubmissionsComponent:', this.sidebarVisible);
    });
  }

  ngOnDestroy(): void {
    if (this.sidebarSub) {
      this.sidebarSub.unsubscribe();
    }
  }

  submissions = [
    {
      title: 'Machine Learning Basics',
      description: 'Submitted final project with 95% accuracy',
      author: 'Alex Johnson',
      authorpp: 'download.jpg',
      time: '2 hours ago',
      iconType: 'green',
      type: 'ml',
      grade: 'D+'
    },
    {
      title: 'Web Development Project',
      description: 'Updated responsive layout based on feedback',
      author: 'Sarah Lee',
      authorpp: 'download.jpg',
      time: 'Yesterday',
      iconType: 'purple',
      type: 'dev',
      grade: 'A-'
    },
    {
      title: 'Data Visualization',
      description: 'Submitted interactive dashboard with 3 chart types',
      author: 'Michael Torres',
      authorpp: 'download.jpg',
      time: '2 days ago',
      iconType: 'green',
      type: 'data',
      grade: 'B+'
    },
    {
      title: 'Mobile App Prototype',
      description: 'Asked for clarification on requirements',
      author: 'Jamie Smith',
      authorpp: 'download.jpg',
      time: '3 days ago',
      iconType: 'blue',
      type: 'data',
      grade: 'C-'
    },
    {
      title: 'Database Design',
      description: 'Submitted optimized schema with documentation',
      author: 'Jamie Smith',
      authorpp: 'download.jpg',
      time: '3 days ago',
      iconType: 'green',
      type: 'data',
      grade: null
    }
  ];
}
