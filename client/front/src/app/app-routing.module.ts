import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';

import { RepoFormComponent } from './repo-form/repo-form.component';
import { AddCollaboratorsComponent } from './add-collaborators/add-collaborators.component';
import { RepoPageComponent } from './repo-page/repo-page.component';
import { RepoCodeComponent } from './repo-code/repo-code.component';
import { RepoSettingsComponent } from './repo-settings/repo-settings.component';
import { RepoListComponent } from './repo-list/repo-list.component';
const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {path: 'dashboard',component: DashboardComponent},
       
    { path: 'create_repo', component: RepoFormComponent },
    //{path: 'addcollab', component: AddCollaboratorsComponent}, // Adjust the component as needed
    { path: 'repo/:owner/:name', component: RepoPageComponent, children: [
      { path: '', redirectTo: 'code', pathMatch: 'full' },
      { path: 'code', component: RepoCodeComponent },
      { path: 'settings', component: RepoSettingsComponent }
    ]
  },  
  {path:'getRepos',component:RepoListComponent},
      
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
