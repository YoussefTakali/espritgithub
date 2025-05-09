import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';
@Component({
  selector: 'app-repo-code',
  templateUrl: './repo-code.component.html',
  styleUrls: ['./repo-code.component.css']
})
export class RepoCodeComponent {
ownerName:string="";
  repoName:string = ''; // Replace with actual repo name from route or service
  collaborators: string[] = [];
  branches: any[] = [];
  filteredCollaborators: any[] = [];
  collab:any;
  repo = {
    name: '',
    auto_init: true,
    gitignore_template: 'Node'
  };
  files: any[] = [];

  constructor(private router: Router, private route: ActivatedRoute,private http: HttpClient) {}
  goToAccessControl() {
    this.router.navigate(['../settings'], { relativeTo: this.route, fragment: 'access' });
  }


  fillRepoInfoFromUrl(url: string) {
    // Match /repo/owner/repoName/
    const match = url.match(/^\/repo\/([^\/]+)\/([^\/]+)/);
    if (match) {
      this.ownerName= match[1];
      this.repoName = match[2];
    } else {
      this.ownerName = '';
      this.repoName = '';
    }
  }
  ngOnInit() {
    this.fillRepoInfoFromUrl(this.router.url);
    console.log('fetching collaborators for', this.ownerName, this.repoName);
   this.getCollaborators()
  
   console.log('getting branches for', this.ownerName, this.repoName);
   this.getBranches();
   console.log('Refreshing collaborators and invitations...');
   console.log(this.branches);
    console.log('*************************************');
    this.filteredCollaborators;
  console.log(this.collaborators)
  console.log('*************************************');
}

      
  getCollaborators() {
    this.http.get<any[]>('/api/github/list-collaborators', {
      params: {
        owner: this.ownerName,
        repo: this.repoName
      }
    }).subscribe(
      data => {
        console.log('collabs response:', data); // <--- Add this!
        this.collaborators = data;
      
        //this.filteredCollaborators = this.collaborators.filter(c => c.login !== this.ownerName);
        console.log('filtred collab', this.filteredCollaborators);
      },
      error => { console.error('Error fetching collabs', error); }
    );
    
   
  }


selectedBranch: string = 'main'; // default

getBranches() {
  console.log('Fetching branches for', this.ownerName, this.repoName);
  this.http.get<any[]>('/api/github/list-branches', {
    params: {
      owner: this.ownerName,
      repo: this.repoName
    }
  }).subscribe(
    data => {
      console.log('Branches response:', data); // <--- Add this!
      this.branches = data;
      if (this.branches.length > 0) {
        this.selectedBranch = this.branches[0].name;
      }
    },
    error => { console.error('Error fetching branches:', error); }
  );
}
showBranchList = false;

toggleBranchList() {
  this.showBranchList = !this.showBranchList;
}

selectBranch(branchName: string) {
  this.selectedBranch = branchName;
  this.showBranchList = false;
  // Optionally: load files for this branch
}
currentPath: string = '';
getRepoContents() {
  return this.http.get<any[]>('/api/github/repo-contents', {
    
    params: { owner:this.ownerName, repo:this.repoName, path:'' }
  }).subscribe(
    data => {
      console.log('repos content', data); // <--- Add this!
      this.files = data;
      console.log('content:', this.files);
     
    },
    error => { console.error('Error fetching branches:', error); }
  );
}
 
}
