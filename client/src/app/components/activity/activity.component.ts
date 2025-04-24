import { Component, Input, SimpleChanges } from '@angular/core';
import { SidebarService } from 'src/app/services/sidebar.service';

@Component({
  selector: 'app-activity',
  templateUrl: './activity.component.html',
  styleUrls: ['./activity.component.css']
})
export class ActivityComponent {
  sidebarVisible: boolean = false;

  @Input() groupTitle: string = '';
  @Input() assignments: any[] = [
    {
      title: 'Machine Learning Basics',
      description: 'Implement a simple neural network for digit recognition',
      submissionRate: 85,
      courseCode: 'CS301',
      dueDate: 'Apr 28, 2025',
      aiAssisted: true
    },
    {
      title: 'Linear Regression',
      description: 'Predict housing prices based on feature inputs',
      submissionRate: 67,
      courseCode: 'CS302',
      dueDate: 'May 4, 2025',
      aiAssisted: false
    },
    {
      title: 'Data Visualization',
      description: 'Build interactive plots using D3.js',
      submissionRate: 72,
      courseCode: 'CS303',
      dueDate: 'May 10, 2025',
      aiAssisted: false
    },
    {
      title: 'Natural Language Processing',
      description: 'Create a sentiment analyzer using NLP techniques',
      submissionRate: 91,
      courseCode: 'CS304',
      dueDate: 'May 15, 2025',
      aiAssisted: true
    },
    {
      title: 'Reinforcement Learning',
      description: 'Train an agent using Q-learning algorithm',
      submissionRate: 59,
      courseCode: 'CS305',
      dueDate: 'May 20, 2025',
      aiAssisted: true
    },
    {
      title: 'AI Ethics & Society',
      description: 'Write a report on ethical implications of AI',
      submissionRate: 78,
      courseCode: 'CS306',
      dueDate: 'May 25, 2025',
      aiAssisted: false
    }
  ]; 
    constructor(private sidebarService: SidebarService) {}
    ngOnInit(): void {
      this.sidebarService.sidebarVisible$.subscribe((visible) => {
        this.sidebarVisible = visible;
        console.log('activity: Sidebar visibility updated to', visible);
      });
    }
}
