import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { AuthGuard } from './keycloak/app.guard';
import { ProjectsComponent } from './pages/projects/projects.component';
import { ProjectDetailsComponent } from './pages/project-details/project-details.component';
import { ClassesComponent } from './pages/classes/classes.component';
import { SubmissionComponent } from './pages/submissions/submission.component';
import { SubmissionDetailsComponent } from './pages/submission-details/submission-details.component';
import { TaskListComponent } from './student/task-list/task-list.component';
import { SubmitComponent } from './student/submit/submit.component';
import { RepoSelectorComponent } from './components/repo-selector/repo-selector.component';
import { RepoBrowserComponent } from './components/repo-browser/repo-browser.component';
import { RepoDashboardComponent } from './components/repo-dashboard/repo-dashboard.component';
import { CommitMessageComponent } from './pages/commit-message/commit-message.component';
import { TaskDashboardComponent } from './components/task-dashboard/task-dashboard.component';
import { CommitHistoryComponent } from './components/commit-history/commit-history.component';
import { CommitDetailComponent } from './components/commit-detail/commit-detail.component';
import { SpecialtiesComponent } from './specialties/specialties.component';
import { LevelsComponent } from './levels/levels.component';
import { StudentsComponent } from './students/students.component';
import { RepoListComponent } from './repo-list/repo-list.component';
import { RepoFormComponent } from './repo-form/repo-form.component';
import { RepoPageComponent } from './repo-page/repo-page.component';
import { RepoCodeComponent } from './repo-code/repo-code.component';
import { RepoSettingsComponent } from './repo-settings/repo-settings.component';

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
      {path:'submissions',component:SubmissionComponent},
      {path:'submissions/:id',component:SubmissionDetailsComponent},
      {path:'tasks-list',component:TaskListComponent},
      {path:'submit/:id',component:SubmitComponent},
      { path: "repo-selector", component: RepoSelectorComponent }, // Explicit route for repo selector
      { path: "repo-browser", component: RepoBrowserComponent },
      { path: 'repo-dashboard', component: RepoDashboardComponent },
       { path: "commit", component: CommitMessageComponent },
      { path: "home", component: TaskDashboardComponent },
  // Add routes for commit history and commit detail
      {    path: "commit-history/:owner/:repo", component: CommitHistoryComponent },
      { path: "commit-detail/:owner/:repo/:commitHash", component: CommitDetailComponent },
      { path: 'specialties', component: SpecialtiesComponent },
      { path: 'levels/:specialtyId', component: LevelsComponent },
      { path: "classes/:specialtyId/:levelId", component: ClassesComponent },
      { path: "students/:classId", component: StudentsComponent },
      { path:'getRepos/:classId', component: RepoListComponent },
      { path: 'create_repo/:classid', component: RepoFormComponent },
      //{path: 'addcollab', component: AddCollaboratorsComponent}, // Adjust the component as needed
      { path: 'repo/:owner/:name', component: RepoPageComponent, children: [
        { path: '', redirectTo: 'code', pathMatch: 'full' },
        { path: 'code', component: RepoCodeComponent },
        { path: 'settings', component: RepoSettingsComponent },
      ]}
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
