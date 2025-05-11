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
  path: string = '';
  contents: any[] = [];
  isFile: boolean = false;
  fileContent: string = '';
  latestCommitBanner: any = null;

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



  

  this.loadContents();

}
getLatestRepoCommit() {
  this.http.get<any>(`/api/github/latest-repo-commit`, {
    params: {
      owner: this.ownerName,
      repo: this.repoName,
      branch: this.selectedBranch
    }
  }).subscribe(data => {
    if (Array.isArray(data) && data.length > 0) {
      const commit = data[0];
      this.latestCommitBanner = {
        message: commit.commit.message,
        author: commit.commit.author.name,
        avatarUrl: commit.author?.avatar_url || 'default-avatar.png', // ✅ FIXED
        time: new Date(commit.commit.author.date),
        sha: commit.sha.substring(0, 7)
      };
    }
  });
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
  console.log('Selected branch:', this.selectedBranch);
  this.loadContents(this.path); // Load contents for the selected branch
  console.log('Loading contents for branch:', this.selectedBranch);

  // Optionally: load files for this branch
}
loadContents(path: string = ''): void {
  this.path = path;
  console.log('Loading contents for:', this.ownerName, this.repoName, path);

  this.http.get<any>(`/api/github/repo-contents`, {
    params: {
      owner: this.ownerName,
      repo: this.repoName,
      path: path,
      branch: this.selectedBranch
    }
  }).subscribe(data => {
    if (Array.isArray(data)) {
      // It's a directory
      this.contents = data;
      this.isFile = false;

      // 🔁 Fetch latest commit message for each item
      this.contents.forEach(item => {
        this.http.get<any>(`/api/github/latest-commit`, {
          params: {
            owner: this.ownerName,
            repo: this.repoName,
            path: item.path,
            branch: this.selectedBranch
          }
        }).subscribe(commitData => {
          if (Array.isArray(commitData) && commitData.length > 0) {
            item.latestCommit = commitData[0].commit.message;
          } else {
            item.latestCommit = 'No recent commit';
          }
        }, error => {
          item.latestCommit = 'Unable to fetch commit';
        });
      });

      // 📄 Load README if exists
      const readme = data.find(item => item.name.toLowerCase() === 'readme.md');
      if (readme) {
        this.http.get<any>(`/api/github/repo-contents`, {
          params: {
            owner: this.ownerName,
            repo: this.repoName,
            path: readme.path,
            branch: this.selectedBranch
          }
        }).subscribe(readmeData => {
          if (readmeData && readmeData.content) {
            this.readmeContent = atob(readmeData.content); // decode base64
          }
        });
      } else {
        this.readmeContent = '';
      }

    } else if (data && data.content) {
      // It's a file
      this.isFile = true;
      this.fileContent = atob(data.content);

      if (path.toLowerCase().endsWith('readme.md')) {
        this.readmeContent = this.fileContent;
      }
    } else {
      // Unexpected response
      this.contents = [];
      this.isFile = false;
      this.readmeContent = '';
    }
    this.getLatestRepoCommit();
  });
}
readmeContent: string = '';}