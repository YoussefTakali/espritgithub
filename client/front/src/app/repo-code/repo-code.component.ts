import { Component } from '@angular/core';

import { Router, ActivatedRoute } from '@angular/router';
@Component({
  selector: 'app-repo-code',
  templateUrl: './repo-code.component.html',
  styleUrls: ['./repo-code.component.css']
})
export class RepoCodeComponent {

  repo = {
    name: '',
    auto_init: true,
    gitignore_template: 'Node'
  };

  constructor(private router: Router, private route: ActivatedRoute) {}
  goToAccessControl() {
    this.router.navigate(['../settings'], { relativeTo: this.route, fragment: 'access' });
  }
}