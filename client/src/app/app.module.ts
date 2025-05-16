import { APP_INITIALIZER, NgModule } from '@angular/core';

import { BrowserModule } from '@angular/platform-browser';

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

@NgModule({
  declarations: [
    AppComponent,
    MainLayoutComponent,
    NavbarComponent,
    SidebarComponent,
    DashboardComponent,
    AssignmentcardComponent,
    StarcardComponent,
    ActivityComponent,
    SubmissionsComponent,
    ProgressComponent,
    GradingComponent,
    ProjectsComponent,


  ],
  imports: [
    BrowserModule,
    KeycloakAngularModule,
    AppRoutingModule,
    HttpClientModule,
    CommonModule,
    AppRoutingModule,
    LucideAngularModule.pick({ 
      BookOpenText, 
   //   GitCommit, 
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
