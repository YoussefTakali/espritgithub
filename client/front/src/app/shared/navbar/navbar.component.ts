import { Component,EventEmitter, Output  } from '@angular/core';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  isSidebarVisible = false;

  @Output() sidebarToggled = new EventEmitter<boolean>();

  onHamburgerClick(event: Event) {
    // Stop event propagation to prevent multiple triggers
    event.preventDefault();
    event.stopPropagation();
    
    // Toggle the state
    this.isSidebarVisible = !this.isSidebarVisible;
    
    console.log('NavbarComponent: Emitting toggle request', this.isSidebarVisible);
    this.sidebarToggled.emit(this.isSidebarVisible);
  }
  dropdownOpen = false;

toggleDropdown() {
  this.dropdownOpen = !this.dropdownOpen;
}

isDarkMode = false;


toggleDarkMode() {
  this.isDarkMode = !this.isDarkMode;
  if (this.isDarkMode) {
    document.body.classList.add('dark-mode');
  } else {
    document.body.classList.remove('dark-mode');
  }
}
}

  


