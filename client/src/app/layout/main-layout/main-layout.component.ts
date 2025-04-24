import { Component } from '@angular/core';

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css']
})
export class MainLayoutComponent {
  isSidebarVisible = false;

  onSidebarToggle(isVisible: boolean) {
    console.log('MainLayoutComponent: Received sidebar visibility', isVisible);
    this.isSidebarVisible = isVisible;
  }
}