import { Component } from '@angular/core';

@Component({
  selector: 'app-repo-form',
  templateUrl: './repo-form.component.html',
  styleUrls: ['./repo-form.component.css']
})
export class RepoFormComponent {
  user = {
    username: 'maramouselati', // Replace with actual username from your auth/user service
    avatarUrl: 'https://avatars.githubusercontent.com/u/000000?v=4' // Replace with actual avatar URL
  };

  repo = {
    name: '',
    description: '',
    visibility: 'public',
    readme: false
  };

  onSubmit() {
    // Handle form submission
    console.log('Repository created:', {
      ...this.repo,
      owner: this.user.username
    });
  }

}