import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
@Component({
  selector: 'app-repo-page',
  templateUrl: './repo-page.component.html',
  styleUrls: ['./repo-page.component.css']
})
export class RepoPageComponent {
  owner = '';
  name = '';
  constructor( private route: ActivatedRoute) {
    this.route.params.subscribe(params => {
      this.owner = params['owner'];
      this.name = params['name'];
    });
  }

}
