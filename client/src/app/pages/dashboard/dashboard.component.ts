import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  currentTime: string = '';
  currentDate: string = '';

  ngOnInit(): void {
    this.updateDateTime();
    setInterval(() => this.updateDateTime(), 1000);
  }

  updateDateTime(): void {
    const now = new Date();
    
    this.currentDate = now.toLocaleDateString(undefined, {
      weekday: 'short',   // e.g., Mon
      year: 'numeric',
      month: 'short',     // e.g., Apr
      day: 'numeric'
    });

    this.currentTime = now.toLocaleTimeString([], {
      hour: '2-digit',
      minute: '2-digit',
    });
  }
}
