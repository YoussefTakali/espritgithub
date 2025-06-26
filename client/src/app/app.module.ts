import { APP_INITIALIZER, NgModule } from '@angular/core';

import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { NavbarComponent } from './shared/navbar/navbar.component';
import { SidebarComponent } from './shared/sidebar/sidebar.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { AssignmentcardComponent } from './components/assignmentcard/assignmentcard.component';
import { StarcardComponent } from './components/starcard/starcard.component';
import { LucideAngularModule, BookOpenText, Calendar, GraduationCap, Route } from 'lucide-angular';
import { ActivityComponent } from './components/activity/activity.component';
import { SubmissionsComponent } from './components/submissions/submissions.component';
import { ProgressComponent } from './components/progress/progress.component';
import { GradingComponent } from './components/grading/grading.component';

import { initializeKeycloak } from './keycloak/app.init';
import { KeycloakAngularModule, KeycloakBearerInterceptor, KeycloakService } from 'keycloak-angular';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { GithubTokenInterceptor } from './keycloak/GithubTokenInterceptor';
import { CommonModule } from '@angular/common';
import { ProjectsComponent } from './pages/projects/projects.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ProjectDetailsComponent } from './pages/project-details/project-details.component';
import { ClassesComponent } from './pages/classes/classes.component';
import { SubmissionComponent } from './pages/submissions/submission.component';
import { SubmissionDetailsComponent } from './pages/submission-details/submission-details.component';
import { DashboardsubmissionsComponent } from './components/dashboardsubmissions/dashboardsubmissions.component';
import { SidebarStudentComponent } from './student/sidebar-student/sidebar-student.component';
import { TaskListComponent } from './student/task-list/task-list.component';
import { TaskDetailComponent } from './student/task-detail/task-detail.component';
import { SubmissionInterfaceComponent } from './student/submission-interface/submission-interface.component';
import { SubmitComponent } from './student/submit/submit.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { ConfirmationDialogComponent } from './confirmation-dialog/confirmation-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatIconModule } from '@angular/material/icon';
import { SafeHtmlPipe } from './pipes/SafeHtmlPipe';
import { TimeAgoPipe } from './pipes/time-ago.pipe';
import { CommitDetailComponent } from './components/commit-detail/commit-detail.component';
import { CommitHistoryComponent } from './components/commit-history/commit-history.component';
import { RecentReposComponent } from './components/recent-repos/recent-repos.component';
import { RepoBrowserComponent } from './components/repo-browser/repo-browser.component';
import { RepoDashboardComponent } from './components/repo-dashboard/repo-dashboard.component';
import { RepoDropdownComponent } from './repo-dropdown/repo-dropdown.component';
import { RepoSelectorComponent } from './components/repo-selector/repo-selector.component';
import { RepositoryReadmeComponent } from './components/repository-readme/repository-readme.component';
import { SidebarReposComponent } from './components/sidebar-repos/sidebar-repos.component';
import { TaskDashboardComponent } from './components/task-dashboard/task-dashboard.component';
import { CommitMessageComponent } from './pages/commit-message/commit-message.component';
import { SpecialtiesComponent } from './specialties/specialties.component';
import { LevelsComponent } from './levels/levels.component';
import { StudentsComponent } from './students/students.component';
import { RepoListComponent } from './repo-list/repo-list.component';
import { RepoFormComponent } from './repo-form/repo-form.component';
import { RepoPageComponent } from './repo-page/repo-page.component';
import { RepoCodeComponent } from './repo-code/repo-code.component';
import { RepoSettingsComponent } from './repo-settings/repo-settings.component';

@NgModule({
  declarations: [
    // Main App Components
    AppComponent,
    MainLayoutComponent,
    
    // Layout Components
    NavbarComponent,
    SidebarComponent,
    
    // Page Components
    DashboardComponent,
    ProjectsComponent,
    ProjectDetailsComponent,
    ClassesComponent,
    SubmissionComponent,
    SubmissionDetailsComponent,
    
    // Dashboard Components
    AssignmentcardComponent,
    StarcardComponent,
    ActivityComponent,
    SubmissionsComponent,
    ProgressComponent,
    GradingComponent,
    DashboardsubmissionsComponent,
    TaskDashboardComponent,
    
    // Repository Components
    RepoSelectorComponent,
    RepoBrowserComponent,
    RepoDashboardComponent,
    RepoDropdownComponent,
    RecentReposComponent,
    RepositoryReadmeComponent,
    SidebarReposComponent,
    
    // Commit Components
    CommitHistoryComponent,
    CommitDetailComponent,
    CommitMessageComponent,
    
    // Student Components
    SidebarStudentComponent,
    TaskListComponent,
    TaskDetailComponent,
    SubmissionInterfaceComponent,
    SubmitComponent,
    
    // New Components
    SpecialtiesComponent,
    LevelsComponent,
    StudentsComponent,
    RepoListComponent,
    RepoFormComponent,
    RepoPageComponent,
    RepoCodeComponent,
    RepoSettingsComponent,
    
    // Shared Components
    ConfirmationDialogComponent,
    
    // Pipes
    SafeHtmlPipe,
    TimeAgoPipe
  ],
  imports: [
    MatIconModule,
    MatProgressBarModule,
    MatDialogModule,
    MatSnackBarModule,
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    CommonModule,
    KeycloakAngularModule,
    AppRoutingModule, // Keep only this one for routing
    LucideAngularModule.pick({ 
      BookOpenText, 
      Calendar, 
      GraduationCap 
    })
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService],
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: KeycloakBearerInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: GithubTokenInterceptor, 
      multi: true,
    },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }