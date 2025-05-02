import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { SidebarService } from 'src/app/services/sidebar.service';

@Component({
  selector: 'app-grading',
  templateUrl: './grading.component.html',
  styleUrls: ['./grading.component.css']
})
export class GradingComponent implements OnInit,OnDestroy {
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
      title: 'Database Design',
      description: 'Submitted optimized schema with documentation',
      author: 'Jamie Smith',
      time: '3 days ago',
      grade: 'A',
      aiSuggestion:55
    },
    {
      title: 'Database Design',
      description: 'Submitted optimized schema with documentation',
      author: 'Jamie Smith',
      time: '3 days ago',
      grade: 'B',
      aiSuggestion:null

    },
    {
      title: 'Database Design',
      description: 'Submitted optimized schema with documentation',
      author: 'Jamie Smith',
      time: '3 days ago',
      grade: 'C',
      aiSuggestion:18
    },
    {
      title: 'Database Design',
      description: 'Submitted optimized schema with documentation',
      author: 'Jamie Smith',
      time: '3 days ago',
      grade: 'D',
      aiSuggestion:40

    },
    {
      title: 'Database Design',
      description: 'Submitted optimized schema with documentation',
      author: 'Jamie Smith',
      time: '3 days ago',
      grade: 'A',
      aiSuggestion:90
    }
  ];
}
