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
import { LucideAngularModule, BookOpenText, GitCommit, Calendar, GraduationCap } from 'lucide-angular';
import { ActivityComponent } from './components/activity/activity.component';
import { SubmissionsComponent } from './components/submissions/submissions.component';
import { ProgressComponent } from './components/progress/progress.component';
import { GradingComponent } from './components/grading/grading.component';

import { initializeKeycloak } from './keycloak/app.init';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';

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
  ],
  imports: [
    BrowserModule,
    KeycloakAngularModule,
    AppRoutingModule,


    AppRoutingModule,
    LucideAngularModule.pick({ 
      BookOpenText, 
      GitCommit, 
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
    }
  ],
  

  bootstrap: [AppComponent]
})
export class AppModule { }
