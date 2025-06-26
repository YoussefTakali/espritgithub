import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RecentReposService, RecentRepo } from 'src/app/services/recent-repos.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';

interface GitHubRepo {
  id: number;
  name: string;
  full_name: string;
  html_url: string;
  description: string;
  updated_at: string;
  visibility: string;
  language: string;
}

@Component({
  selector: 'app-sidebar-repos',
  templateUrl: './sidebar-repos.component.html',
  styleUrls: ['./sidebar-repos.component.css']
})
export class SidebarReposComponent implements OnInit {
  repositories: { name: string, fullName: string }[] = [];
  showAllRepos = false;
  maxVisibleRepos = 10;
  isLoading = false;
  searchQuery = '';

  constructor(
    private recentReposService: RecentReposService,
    private router: Router,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    // Add test data for immediate display

    // First try to load from recent repos
    this.recentReposService.recentRepos$.subscribe(repos => {
      if (repos.length > 0) {
        // Combine with test data to ensure we have enough repos to show
        const recentRepoMap = new Map(repos.map(repo => [repo.fullName, { name: repo.name, fullName: repo.fullName }]));

        // Add any test repos that aren't in recent repos
        this.repositories.forEach(repo => {
          if (!recentRepoMap.has(repo.fullName)) {
            recentRepoMap.set(repo.fullName, repo);
          }
        });

        this.repositories = Array.from(recentRepoMap.values());
      } else if (this.repositories.length === 0) {
        // If no recent repos and no test data, try to load from GitHub API
        this.loadRepositories();
      }
    });
  }

  /**
   * Add test repositories for demonstration
   */

  loadRepositories(): void {
    this.isLoading = true;

    // Use backend API instead of direct GitHub API
    this.http.get<GitHubRepo[]>('http://localhost:8080/api/users/salmabm/repos')
      .subscribe({
        next: (repos) => {
          this.repositories = repos.map(repo => ({
            name: repo.name,
            fullName: repo.full_name
          }));
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error fetching repositories:', error);
          this.isLoading = false;
        }
      });
  }

  navigateToRepo(fullName: string): void {
    // Extract name from fullName (owner/name)
    const name = fullName.split('/')[1];

    // Add to recent repos
    this.recentReposService.addRecentRepo(name, fullName);

    // Navigate to repo browser
    this.router.navigate(['repo-browser'], {
      queryParams: {
        repo: fullName
      }
    });
  }

  toggleShowAll(): void {
    this.showAllRepos = !this.showAllRepos;
  }

  get visibleRepos(): { name: string, fullName: string }[] {
    let filteredRepos = this.repositories;

    // Apply search filter if there's a query
    if (this.searchQuery) {
      const query = this.searchQuery.toLowerCase();
      filteredRepos = filteredRepos.filter(repo =>
        repo.name.toLowerCase().includes(query) ||
        repo.fullName.toLowerCase().includes(query)
      );
    }

    // Limit number of repos shown if not showing all
    if (!this.showAllRepos) {
      return filteredRepos.slice(0, this.maxVisibleRepos);
    }

    return filteredRepos;
  }
}
