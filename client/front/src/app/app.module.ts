import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms'; //
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AppProfessorDashboardComponent } from './app-professor-dashboard/app-professor-dashboard.component';
import { NavbarComponent } from './shared/navbar/navbar.component';
import { SidebarComponent } from './shared/sidebar/sidebar.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { RepoFormComponent } from './repo-form/repo-form.component';

@NgModule({
  declarations: [
    AppComponent,
    AppProfessorDashboardComponent,
    NavbarComponent,
    SidebarComponent,
    MainLayoutComponent,
    DashboardComponent,
    RepoFormComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,// Import FormsModule for ngModel

  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
