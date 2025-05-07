import { Component } from '@angular/core';

@Component({
  selector: 'app-add-collaborators',
  templateUrl: './add-collaborators.component.html',
  styleUrls: ['./add-collaborators.component.css']
})
export class AddCollaboratorsComponent {


  repoName = 'test'; // Replace with actual repo name, maybe from route params
  collaborator = '';
  collaborators: string[] = [];

  addCollaborator() {
    if (this.collaborator && !this.collaborators.includes(this.collaborator)) {
      this.collaborators.push(this.collaborator);
      this.collaborator = '';
    }
  }

  removeCollaborator(name: string) {
    this.collaborators = this.collaborators.filter(c => c !== name);
  }
}