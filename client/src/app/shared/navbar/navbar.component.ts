import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  isSidebarVisible = false;
  dropdownOpen = false;
  
  // Add properties for user data
  userName: string | null = null;
  userAvatar: string | null = null;
  githubUsername: string | null = null;

  @Output() sidebarToggled = new EventEmitter<boolean>();
  @Output() toggleSidebar = new EventEmitter<void>();

  constructor(private http: HttpClient) {}

  ngOnInit() {
    // Fetch GitHub user data when component initializes
    this.fetchGitHubUserData();
  }

  // Fetch GitHub user data
  fetchGitHubUserData() {
    // You can replace this with your actual GitHub username or get it from a service/localStorage
    this.githubUsername = "salmabm"; // Replace with actual username or get from auth service

    if (this.githubUsername) {
      this.http.get(`https://api.github.com/users/${this.githubUsername}`).subscribe(
        (response: any) => {
          this.userName = response.name || response.login;
          this.userAvatar = response.avatar_url;
        },
        (error) => {
          console.error("Error fetching GitHub user data:", error);
          // Fallback to default values if GitHub API fails
          this.userName = "Guest User";
          this.userAvatar = null;
        }
      );
    }
  }

  onHamburgerClick(event: Event) {
    // Stop event propagation to prevent multiple triggers
    event.preventDefault();
    event.stopPropagation();
    
    // Toggle the state
    this.isSidebarVisible = !this.isSidebarVisible;
    
    console.log('NavbarComponent: Emitting toggle request', this.isSidebarVisible);
    this.sidebarToggled.emit(this.isSidebarVisible);
  }

  toggleDropdown() {
    this.dropdownOpen = !this.dropdownOpen;
  }

  // Get user initials for avatar fallback
  getUserInitials(): string {
    if (this.userName) {
      return this.userName
        .split(" ")
        .map((name) => name[0])
        .join("")
        .toUpperCase()
        .substring(0, 2);
    }
    return "GU"; // Default Guest User
  }
}
