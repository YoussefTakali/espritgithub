import { Component, Input, OnChanges, SimpleChanges, OnInit } from '@angular/core';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnChanges, OnInit {
  @Input() isSidebarVisible: boolean = false;

  userRole: string | null = null;
  userId: string | null = null;

  ngOnInit(): void {
    // Fetch userRole and userId from localStorage on component initialization
    this.userRole = localStorage.getItem('role');
    this.userId = localStorage.getItem('id');

    console.log('User Role:', this.userRole);
    console.log('User ID:', this.userId);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isSidebarVisible']) {
      console.log('Sidebar visibility changed:', this.isSidebarVisible);
    }
  }
}
