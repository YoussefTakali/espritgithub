import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
@Component({
  selector: 'app-repo-list',
  templateUrl: './repo-list.component.html',
  styleUrls: ['./repo-list.component.css']
})
export class RepoListComponent {


  allRepos: any[] = [];
  displayedRepos: any[] = [];
  searchTerm: string = '';
  ownerName: string = '';
  repoName: string = '';
  path: string = '';
  contents: any[] = [];
  isFile: boolean = false;
  fileContent: string = '';

  constructor(private http: HttpClient,public router:Router) {}

  ngOnInit() {
    this.loadRepos();
   

  }

  loadRepos() {
    this.http.get<any[]>('/api/github/user-repos', {
      params: { username: 'maramoueslati' }
    }).subscribe(data => {
      this.allRepos = data;
      this.displayedRepos = this.allRepos.slice(0, 10); // Show 10 latest by default
    });
  }

  onSearch() {
    if (this.searchTerm.trim()) {
      this.displayedRepos = this.allRepos.filter(repo =>
        repo.name.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    } else {
      this.displayedRepos = this.allRepos.slice(0, 10); // Reset to 10 latest
    }
  }
}
