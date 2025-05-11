import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
@Component({
  selector: 'app-repo-page',
  templateUrl: './repo-page.component.html',
  styleUrls: ['./repo-page.component.css']
})
export class RepoPageComponent {
ownerName = '';
repoName = '';
path = '';
contents: any[] = [];
isFile = false;
fileContent = '';

  constructor( private route: ActivatedRoute,private http: HttpClient, private router: Router) {
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
  onInit() { this.fillRepoInfoFromUrl(this.router.url);
    console.log('*******MARAMMMMMM***********');
    console.log('Hello from RepoPageComponent!');
    console.log(this.ownerName);
    console.log(this.repoName);
}

}


