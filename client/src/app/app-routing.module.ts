import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { AuthGuard } from './keycloak/app.guard';
import { ProjectsComponent } from './pages/projects/projects.component';
import { ProjectDetailsComponent } from './pages/project-details/project-details.component';
import { ClassesComponent } from './pages/classes/classes.component';
import { ListUserComponent } from './components/users/list-user/list-user.component';
import { AddGithubInfoComponent } from './components/users/add-github-info/add-github-info.component';

const routes: Routes = [
  {
    path: '',
    component:MainLayoutComponent,
    canActivate: [AuthGuard], // ✅ protection par Keycloak

    children:[
      {path:'', redirectTo:'dashboard',pathMatch:'full'},
      { path:'dashboard',component:DashboardComponent},
      { path:'projects',component:ProjectsComponent},
      { path:'project-details/:id',component:ProjectDetailsComponent},
      {path:'classes',component:ClassesComponent},
      {path:'users',component:ListUserComponent},
      {path:'add-github-info',component:AddGithubInfoComponent},


    ]
  }
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
