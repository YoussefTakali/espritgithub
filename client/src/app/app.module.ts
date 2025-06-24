import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { RepoBrowserComponent } from './components/repo-browser/repo-browser.component';
import { RepoDashboardComponent } from './components/repo-dashboard/repo-dashboard.component';
import { RepoSelectorComponent } from './components/repo-selector/repo-selector.component';
import { RecentReposComponent } from './components/recent-repos/recent-repos.component';
import { RepoDropdownComponent } from './components/repo-dropdown/repo-dropdown.component';
import { SidebarReposComponent } from './components/sidebar-repos/sidebar-repos.component';
import { CommitMessageComponent } from './pages/commit-message/commit-message.component';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from './shared/navbar/navbar.component';
import { SidebarComponent } from './shared/sidebar/sidebar.component';
import { HttpClientModule } from '@angular/common/http';
import { ClickOutsideDirective } from './directives/click-outside.directive';
import { RepositoryReadmeComponent } from './components/repository-readme/repository-readme.component';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TestComponent } from './pages/test/test.component';
import { CommitHistoryComponent } from './components/commit-history/commit-history.component';
import { CommitDetailComponent } from './components/commit-detail/commit-detail.component';
import { TaskDashboardComponent } from './components/task-dashboard/task-dashboard.component';
@NgModule({
  declarations: [
    AppComponent,
    RepoBrowserComponent,
    RepoDashboardComponent,
    RepoSelectorComponent,
    RecentReposComponent,
    RepoDropdownComponent,
    SidebarReposComponent,
    CommitMessageComponent,
    NavbarComponent,
    SidebarComponent,
    TestComponent,
    ClickOutsideDirective,
    RepositoryReadmeComponent,
    CommitHistoryComponent,
    CommitDetailComponent,
    TaskDashboardComponent,
    SidebarComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,FormsModule,
    HttpClientModule,CommonModule,MatIconModule, BrowserAnimationsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
