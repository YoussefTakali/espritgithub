import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

@Component({
  selector: 'app-repo-code',
  templateUrl: './repo-code.component.html',
  styleUrls: ['./repo-code.component.css']
})
export class RepoCodeComponent {
  ownerName: string = "";
  repoName: string = '';
  collaborators: string[] = [];
  branches: any[] = [];
  filteredCollaborators: any[] = [];
  collab: any;
  repo = {
    name: '',
    auto_init: true,
    gitignore_template: 'Node'
  };
  files: any[] = [];
  path: string = '';
  contents: any[] = [];
  isFile: boolean = false;
  isImage: boolean = false;
  fileContent: string = ' ';
  latestCommitBanner: any = null;
  count: number = 0;
  selectedBranch: string = 'main';
  readmeContent: string = '';
  showBranchList = false;

  constructor(private router: Router, private route: ActivatedRoute, private http: HttpClient) {}

  ngOnInit() {
    this.fillRepoInfoFromUrl(this.router.url);
    this.getCollaborators();
    this.getBranches();
    this.loadContents();
  }

  fillRepoInfoFromUrl(url: string) {
    const match = url.match(/^\/repo\/([^\/]+)\/([^\/]+)/);
    if (match) {
      this.ownerName = match[1];
      this.repoName = match[2];
    }
  }

  goToAccessControl() {
    this.router.navigate(['../settings'], { relativeTo: this.route, fragment: 'access' });
  }

  getCollaborators() {
    this.http.get<any[]>('/api/github/list-collaborators', {
      params: {
        owner: this.ownerName,
        repo: this.repoName
      }
    }).subscribe(data => {
      this.collaborators = data;
    }, error => {
      console.error('Error fetching collaborators:', error);
    });
  }

  getBranches() {
    this.http.get<any[]>('/api/github/list-branches', {
      params: {
        owner: this.ownerName,
        repo: this.repoName
      }
    }).subscribe(data => {
      this.branches = data;
      if (this.branches.length > 0) {
        this.selectedBranch = this.branches[0].name;
      }
    }, error => {
      console.error('Error fetching branches:', error);
    });
  }

  toggleBranchList() {
    this.showBranchList = !this.showBranchList;
  }

  selectBranch(branchName: string) {
    this.selectedBranch = branchName;
    this.showBranchList = false;
    this.loadContents(this.path);
  }

  getLatestRepoCommit(): void {
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
          avatarUrl: commit.author?.avatar_url || 'default-avatar.png',
          time: new Date(commit.commit.author.date),
          sha: commit.sha.substring(0, 7),
          count: 1
        };
      }
    });
  }

  getCommitCount(owner: string, repo: string, path: string, branch: string): Observable<number> {
    const params: any = { owner, repo, branch };
    if (path) params.path = path;

    return this.http.get<{ count: number }>('/api/github/commit-count', { params }).pipe(
      map(response => response.count),
      catchError(error => {
        console.error('Error fetching commit count:', error);
        return of(0);
      })
    );
  }

  loadContents(path: string = ''): void {
    this.path = path;
    this.http.get<any>(`/api/github/repo-contents`, {
      params: {
        owner: this.ownerName,
        repo: this.repoName,
        path: path,
        branch: this.selectedBranch
      }
    }).subscribe(data => {
      if (Array.isArray(data)) {
        this.contents = data;
        this.isFile = false;

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
              this.getCommitCount(this.ownerName, this.repoName, item.path, this.selectedBranch).subscribe(count => {
                item.commitCount = count;
              });
            } else {
              item.latestCommit = 'No recent commit';
            }
          }, error => {
            item.latestCommit = 'Unable to fetch commit';
          });
        });

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
              this.readmeContent = atob(readmeData.content);
            }
          });
        } else {
          this.readmeContent = '';
        }
      } else if (data && data.content) {
        this.isFile = true;
        if (this.isImageFile(data.path)) {
          const binaryData = atob(data.content);
          const bytes = new Uint8Array(binaryData.length);
          for (let i = 0; i < binaryData.length; i++) {
            bytes[i] = binaryData.charCodeAt(i);
          }
          const blob = new Blob([bytes], { type: this.getContentType(data.path) });
          this.fileContent = URL.createObjectURL(blob);
          console.log('Image Blob URL:', this.fileContent);

          this.isImage = true;
        } else {
          this.fileContent = atob(data.content);
          this.isImage = false;
          if (path.toLowerCase().endsWith('readme.md')) {
            this.readmeContent = this.fileContent;
          }
        }
      } else {
        this.contents = [];
        this.isFile = false;
        this.readmeContent = '';
        this.fileContent = 'null';
      }
      this.getLatestRepoCommit();
    });
  }

  private isImageFile(path: string): boolean {
    const imageExtensions = ['.png', '.jpg', '.jpeg', '.gif', '.bmp', '.svg'];
    const extension = path.substring(path.lastIndexOf('.')).toLowerCase();
    return imageExtensions.includes(extension);
  }

  private getContentType(path: string): string {
    const extension = path.substring(path.lastIndexOf('.')).toLowerCase();
    switch (extension) {
      case '.png': return 'image/png';
      case '.jpg':
      case '.jpeg': return 'image/jpeg';
      case '.gif': return 'image/gif';
      case '.svg': return 'image/svg+xml';
      case '.bmp': return 'image/bmp';
      default: return 'application/octet-stream';
    }
  }
  goBack() {
    if (!this.path) return; // already at root
  
    const lastSlash = this.path.lastIndexOf('/');
    this.loadContents(lastSlash === -1 ? '' : this.path.substring(0, lastSlash));
  }
}
