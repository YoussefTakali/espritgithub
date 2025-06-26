import { Component, Input, OnInit } from '@angular/core';
import { RepositoryDescriptionService, RepositoryDescription } from '../../services/repository-description.service';

@Component({
  selector: 'app-repository-readme',
  templateUrl: './repository-readme.component.html',
  styleUrls: ['./repository-readme.component.css']
})
export class RepositoryReadmeComponent implements OnInit {
  @Input() owner: string = '';
  @Input() repositoryName: string = '';

  description: RepositoryDescription = {
    owner: '',
    repositoryName: '',
    description: '',
    exists: false
  };

  isEditing = false;
  isLoading = false;
  isSaving = false;
  error = '';
  successMessage = '';

  constructor(private descriptionService: RepositoryDescriptionService) { }

  ngOnInit(): void {
    if (this.owner && this.repositoryName) {
      this.loadDescription();
    }
  }

  loadDescription(): void {
    this.isLoading = true;
    this.error = '';

    this.descriptionService.getDescription(this.owner, this.repositoryName).subscribe({
      next: (data) => {
        this.description = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading description:', error);
        // Set default empty description instead of showing error
        this.description = {
          owner: this.owner,
          repositoryName: this.repositoryName,
          description: '',
          exists: false
        };
        this.isLoading = false;
        // Don't show error message for missing descriptions - it's normal
      }
    });
  }

  startEditing(): void {
    this.isEditing = true;
    this.error = '';
    this.successMessage = '';
  }

  cancelEditing(): void {
    this.isEditing = false;
    this.loadDescription(); // Reload original description
  }

  saveDescription(): void {
    if (!this.description.description.trim()) {
      this.error = 'Description cannot be empty';
      return;
    }

    this.isSaving = true;
    this.error = '';

    console.log('Saving description for:', this.owner, this.repositoryName);
    console.log('Description content:', this.description.description);

    this.descriptionService.saveDescription(
      this.owner,
      this.repositoryName,
      this.description.description,
      'current-user' // TODO: Replace with actual username
    ).subscribe({
      next: (data) => {
        console.log('Description saved successfully:', data);
        this.description = data;
        this.isEditing = false;
        this.isSaving = false;
        this.successMessage = 'Description saved successfully!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        console.error('Error saving description:', error);
        console.error('Error status:', error.status);
        console.error('Error message:', error.message);
        console.error('Full error object:', error);

        let errorMessage = 'Failed to save description';
        if (error.status === 0) {
          errorMessage = 'Cannot connect to server. Please check if the backend is running on http://localhost:8080';
        } else if (error.status === 404) {
          errorMessage = 'API endpoint not found. Please check if the backend has the repository description endpoints.';
        } else if (error.status === 500) {
          errorMessage = 'Server error. Please check the backend logs.';
        } else if (error.error && error.error.error) {
          errorMessage = error.error.error;
        }

        this.error = errorMessage;
        this.isSaving = false;
      }
    });
  }

  deleteDescription(): void {
    if (!confirm('Are you sure you want to delete this description?')) {
      return;
    }

    this.descriptionService.deleteDescription(this.owner, this.repositoryName).subscribe({
      next: () => {
        this.description = {
          owner: this.owner,
          repositoryName: this.repositoryName,
          description: '',
          exists: false
        };
        this.isEditing = false;
        this.successMessage = 'Description deleted successfully!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        console.error('Error deleting description:', error);
        this.error = 'Failed to delete description';
      }
    });
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return '';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
