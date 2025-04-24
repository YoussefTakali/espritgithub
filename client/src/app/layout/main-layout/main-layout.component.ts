import { Component } from '@angular/core';
import { SidebarService } from 'src/app/services/sidebar.service';

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css']
})
export class MainLayoutComponent {
  isSidebarVisible = false;

  constructor(private sidebarService: SidebarService) {}

  onSidebarToggle(isVisible: boolean) {
    this.isSidebarVisible = isVisible;
    this.sidebarService.toggleSidebar(isVisible); // 👈 broadcast to everyone
  }
}