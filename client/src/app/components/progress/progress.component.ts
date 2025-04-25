import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { SidebarService } from 'src/app/services/sidebar.service';

@Component({
  selector: 'app-progress',
  templateUrl: './progress.component.html',
  styleUrls: ['./progress.component.css']
})
export class ProgressComponent implements OnInit,OnDestroy {
  sidebarVisible: boolean = false;
  private sidebarSub!: Subscription;

  constructor(private sidebarService: SidebarService) {}

  ngOnInit(): void {
    this.sidebarSub = this.sidebarService.sidebarVisible$.subscribe((visible:boolean) => {
      this.sidebarVisible = visible;
      console.log('Sidebar visibility in ProgressComponent:', this.sidebarVisible);
    });
  }

  ngOnDestroy(): void {
    if (this.sidebarSub) {
      this.sidebarSub.unsubscribe();
    }
  }
  classrepo = [
    {
      name: 'cs301-machine-learning',
      description: 'Course materials and assignments for Machine Learning',
      code: 'CS301',
      students: 24,
      forks: 18,
      ai_assited: true
    },
    {
      name: 'cs201-database-systems',
      description: 'Database design assignments and examples',
      code: 'CS201',
      students: 45,
      forks: 36,
      ai_assited: true
    },
    {
      name: 'cs401-web-development',
      description: 'Web development projects and resources',
      code: 'CS401',
      students: 32,
      forks: 28,
      ai_assited: true
    },
    {
      name: 'cs501-advanced-topics',
      description: 'Research papers and advanced project materials',
      code: 'CS501',
      students: 18,
      forks: 12,
      ai_assited: null
    },
    {
      name: 'cs501-advanced-topics',
      description: 'Research papers and advanced project materials',
      code: 'CS501',
      students: 18,
      forks: 12,
      ai_assited: null
    },
    {
      name: 'cs501-advanced-topics',
      description: 'Research papers and advanced project materials',
      code: 'CS501',
      students: 18,
      forks: 12,
      ai_assited: null
    }
  ];
}
