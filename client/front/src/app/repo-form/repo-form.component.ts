import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
@Component({
  selector: 'app-repo-form',
  templateUrl: './repo-form.component.html',
  styleUrls: ['./repo-form.component.css']
})
export class RepoFormComponent {
  user = {
    username: 'maramoueslati', // Replace with actual username from your auth/user service
    avatarUrl: 'https://avatars.githubusercontent.com/u/000000?v=4' // Replace with actual avatar URL
  };
  err:string = '';

  repo = {
    name: '',
    description: '',
    visibility: '',
    auto_init: false,
    gitignore_template: ''    // for .gitignore
   
  };
  constructor(private router: Router , private http: HttpClient) {}

  onSubmit() {
    console.log(this.repo.visibility);
    this.http.post('/api/github/create-repo', {
      name: this.repo.name,
      ownerName: this.user.username,
      
      description: this.repo.description,
      isPrivate: this.repo.visibility === 'private' ,
      auto_init: this.repo.auto_init, // send as boolean
      gitignore_template: this.repo.gitignore_template ? 'Node' : null // or let user pick template
    }).subscribe((response: any) => {
      this.router.navigate(['/repo', this.user.username, this.repo.name]);
    });



}
  
}