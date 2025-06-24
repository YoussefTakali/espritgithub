import { NgModule } from "@angular/core"
import { RouterModule, type Routes } from "@angular/router"
import { RepoSelectorComponent } from "./components/repo-selector/repo-selector.component"
import { RepoBrowserComponent } from "./components/repo-browser/repo-browser.component"
import { RepoDashboardComponent } from "./components/repo-dashboard/repo-dashboard.component"
import { CommitMessageComponent } from "./pages/commit-message/commit-message.component"
import { CommitHistoryComponent } from "./components/commit-history/commit-history.component"
import { CommitDetailComponent } from "./components/commit-detail/commit-detail.component"
import { TaskDashboardComponent } from "./components/task-dashboard/task-dashboard.component"

const routes: Routes = [
  { path: "", component: RepoSelectorComponent },
  { path: "repo-selector", component: RepoSelectorComponent }, // Explicit route for repo selector
  { path: "repo-browser", component: RepoBrowserComponent },
  { path: "repo-dashboard", component: RepoDashboardComponent },
  { path: "commit", component: CommitMessageComponent },
    { path: "home", component: TaskDashboardComponent },
  // Add routes for commit history and commit detail
  { path: "commit-history/:owner/:repo", component: CommitHistoryComponent },
  { path: "commit-detail/:owner/:repo/:commitHash", component: CommitDetailComponent },
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
