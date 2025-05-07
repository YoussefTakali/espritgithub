import { Component,EventEmitter, Output  } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
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
repoOwner: string="";
repoName: string="";
onInit() {
  console.log('Hello from NavbarComponent!');
  console.log(this.repoOwner);
  console.log(this.repoName);
  console.log('router.url', this.router.url);
}

constructor(private route: ActivatedRoute, public router: Router) {
  

  //  this.repoOwner =  this.route.snapshot.params['owner'];
    // this.repoName = this.route.snapshot.params['name'];
  ;
}
fillRepoInfoFromUrl(url: string) {
  // Match /repo/owner/repoName/...
  const match = url.match(/^\/repo\/([^\/]+)\/([^\/]+)/);
  if (match) {
    this.repoOwner = match[1];
    this.repoName = match[2];
  } else {
    this.repoOwner = '';
    this.repoName = '';
  }
}
ngOnInit() {
  this.fillRepoInfoFromUrl(this.router.url);
}
showRepoPath(): boolean {
  const codePath =   '/repo/'+this.repoOwner+'/'+this.repoName+'/code';
  const settingsPath = '/repo/'+this.repoOwner+'/'+this.repoName+'/settings';
  console.log('owneer:', this.repoOwner);
  console.log('name:', this.repoName);
  console.log('Current URL:', this.router.url);
  //console.log('Code Path:', codePath);
  console.log('Settings Path:', settingsPath);
  return  this.router.url===codePath || this.router.url === settingsPath;
}
}

  


