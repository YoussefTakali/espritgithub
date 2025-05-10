import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
@Component({
  selector: 'app-repo-settings',
  templateUrl: './repo-settings.component.html',
  styleUrls: ['./repo-settings.component.css']
})
export class RepoSettingsComponent {
  activeTab: 'general' | 'collaborators' | 'delete' |'branches'| 'commits' = 'general' ;
   ownerName:string="";
  repoName:string = ''; // Replace with actual repo name from route or service
  collaborators: string[] = [];
  collaborator = '';
  showAddCollab = false;
  inviteError:string= '';
  showDeleteConfirm = false;
  deleteInput = '';
  collaborators1: any[] = [];
  invitations: any[] = [];
  SuccessError:string= '';
  branche_add_success:string= '';
  branche_add_error:string= '';
  filteredCollaborators: any[] = [];
  branche_remove_success:string= '';
  branche_remove_error:string= '';
  branche_add_success_2:string= '';
  branche_add_error_2:string= '';
  updateRepoName() {
    // Implement update logic here
    alert('Repository renamed to: ' + this.repoName);
  }
  constructor(private act: ActivatedRoute,private http: HttpClient,public router:Router) { }

  fillRepoInfoFromUrl(url: string) {
    // Match /repo/owner/repoName/
    const match = url.match(/^\/repo\/([^\/]+)\/([^\/]+)/);
    if (match) {
      this.ownerName= match[1];
      this.repoName = match[2];
    } else {
      this.ownerName = '';
      this.repoName = '';
    }
  }
  ngOnInit() {
    this.fillRepoInfoFromUrl(this.router.url);
   this.getCollaborators();

   console.log('Refreshing collaborators and invitations...');
   console.log('Refreshing collaborators*************');
   setTimeout(() => {
    this.getBranches();
  }, 0);
    console.log('Refreshing branches*************');
 
    }
    
    getCollaborators() {
      this.http.get<any[]>('/api/github/list-collaborators', {
        params: {
          owner: this.ownerName,
          repo: this.repoName
        }
      }).subscribe(data => {
        this.collaborators1 = data;
   
      
      });
//get invitations
      this.http.get<any[]>('/api/github/list-invitations', { params: { owner:this.ownerName, repo:this.repoName } })
      .subscribe(invitations => {
        this.invitations = invitations;
    
        console.log('Invitations:', this.invitations);
      });
    }
  
//add collaborator
  addCollaborator() {
    if (this.collaborator.includes('@')) {
      this.inviteError = 'Inviting by email is not supported. Please enter a GitHub username.';
      return;
    }
    
  
    if (this.collaborator === this.ownerName) {
      this.inviteError = 'You cannot add yourself as a collaborator.';
      this.collaborator = '';
      return;}
    this.http.post('/api/github/add-collaborator', {
      owner: this.ownerName,
      repo: this.repoName,
      username: this.collaborator,
      permission: 'push' // or 'pull', 'admin'
    }).subscribe(response => {
      this.SuccessError = this.collaborator+"  was  added successfully";
    });

  }
// Remove collaborator
  remove_collab(username: string) {
    this.http.delete('/api/github/remove-collaborator', {
      params: {
        owner: this.ownerName,
        repo: this.repoName,
        username: username
      }
    }).subscribe(response => {
      // handle response, e.g., refresh the lis
      this.removeBranch(username);
    });

   
  }
  
  // Invitation removeal
  remove_invitation(invitation: any) {
    this.http.delete('/api/github/remove-invitation', {
      params: {
        owner: this.ownerName,
        repo: this.repoName,
        invitationId: invitation.id
      }
    }).subscribe(response => {
      // handle response, e.g., refresh the list
    });

   
  }


selectedInvite: any = null; // The invite to remove
showRemoveInviteModal = false;

openRemoveInviteModal(invite: any) {
  console.log("********************************",invite.id); 
  this.selectedInvite = invite;
  this.showRemoveInviteModal = true;
}

closeRemoveInviteModal() {
  
  this.selectedInvite = null;
  this.showRemoveInviteModal = false;
}

confirmRemoveInvite() {
  
  this.remove_invitation(this.selectedInvite);

  this.closeRemoveInviteModal();

}
selectedCollab: any = null; 
showRemoveCollabModal = false;
openRemoveCollabModal(c: string) {
  this.selectedCollab = c;
  this.showRemoveCollabModal = true;
}

closeCollabModal() {
  this.selectedCollab = null;
  this.showRemoveCollabModal = false;
}

confirmRemoveCollab() {
 
  this.remove_collab(this.selectedCollab);
  this.closeCollabModal();
}
  // Delete repository
  confirmDelete() {
  
    alert('Repository deleted!');
    this.showDeleteConfirm = false;
    this.deleteInput = '';
  }
  refreshCollaboratorsAndInvitations() {
    this.getCollaborators
  }


 
  branches: any[] = [];

  getBranches() {
    console.log('Fetching branches for', this.ownerName, this.repoName);
    this.http.get<any[]>('/api/github/list-branches', {
      params: {
        owner: this.ownerName,
        repo: this.repoName
      }
    }).subscribe(data => {
      this.branches = data;
      console.log("*******************************");
      console.log('Branches:', this.branches);
      // If you want just the names:
      // this.branchNames = data.map(branch => branch.name);
    });
  }

  addBranchForCollaborators() {
    const excludedUser = this.ownerName;
this.filteredCollaborators = this.collaborators1.filter(c => c.login !== excludedUser);
    // Directly call the backend to get the latest SHA
    console.log('Fetching latest SHA for', this.ownerName, this.repoName);
    this.http.get<any>('/api/github/latest-sha', {
      params: { owner: this.ownerName, repo: this.repoName, baseBranch: 'main' }
    }).subscribe(shaRes => {
      const sha = shaRes.object.sha;
      for (const collaborator of this.filteredCollaborators) {
        const collaboratorUsername = collaborator.login;
  
        // Directly call the backend to check if the branch exists
        this.http.get<boolean>('/api/github/branch-exists', {
          params: {
            owner: this.ownerName,
            repo: this.repoName,
            branch: collaboratorUsername
          }
        }).subscribe(exists => {
          if (!exists ) {
            // Directly call the backend to create the branch
            console.log('Creating branch for', collaboratorUsername);
            this.http.post('/api/github/create-branch', null, {
              params: {
                owner: this.ownerName,
                repo: this.repoName,
                newBranch: collaboratorUsername,
                sha: sha
              }
            }).subscribe(() => {
              this.branche_add_success='Branch created for ' + collaboratorUsername;
            
              
            });
          } else {
           this. branche_add_error='Branch already exists for ' + collaboratorUsername;
          }
        });
      }
    });
  }
  
  createBranch() {
    this.http.get<any>('/api/github/latest-sha', {
      params: {
        owner: this.ownerName,
        repo: this.repoName,
        baseBranch: 'main'
      }
    }).subscribe(shaRes => {
      const sha = shaRes.object.sha;
  
      if (!this.newBranchName) return;
  
      const params = {
        owner: this.ownerName,
        repo: this.repoName,
        newBranch: this.newBranchName,
        sha: sha
      };
  
      this.http.post('/api/github/create-branch', null, { params }).subscribe(() => {
        this.branche_add_success_2 = ` Branch for ${this.newBranchName} was created successfully`;
        this.getBranches();
        this.toggleCreateBranch();
      }, err => {
        this.branche_add_error_2 = 'Error creating branch for ' + this.newBranchName;
      });
    });
  }
  
  newBranchName = '';
showCreateBranchInput = false;
branchSearch = '';

toggleCreateBranch() {
  this.showCreateBranchInput = !this.showCreateBranchInput;
  this.newBranchName = '';
}

  setActiveTab() {
    this.activeTab = "branches"
   
      this.getBranches();
    
  }
  defaultBranch: string = 'main';

branchSearchTerm: string = '';

filteredBranches() {
  return this.branches.filter(branch =>
    branch.name !== this.defaultBranch &&
    (!this.branchSearch || branch.name.includes(this.branchSearch))
  );
}
  deleteBranch(branchName: string) {}
  selectedBranchToRemove: string | null = null;
  showRemoveBranchModal: boolean = false;
  
  openRemoveBranchModal(branchName: string) {
    this.selectedBranchToRemove = branchName;
    this.showRemoveBranchModal = true;
  }
  
  closeRemoveBranchModal() {
    this.selectedBranchToRemove = null;
    this.showRemoveBranchModal = false;
  }
  
  confirmRemoveBranch() {
    if (!this.selectedBranchToRemove) return;
  
    this.http.delete('/api/github/delete-branch', {
      params: {
        owner: this.ownerName,
        repo: this.repoName,
        branch: this.selectedBranchToRemove
      }
    }).subscribe(() => {
      this.branche_remove_success = `${this.selectedBranchToRemove} was deleted successfully`;
      this.getBranches(); // Refresh list
      this.closeRemoveBranchModal();
    }, err => {
      this.branche_remove_error = `Error deleting branch ${this.selectedBranchToRemove}`;
      this.closeRemoveBranchModal();
    });
  }
  removeBranch(branchName: string) {
    this.http.delete('/api/github/delete-branch', {
      params: {
        owner: this.ownerName,
        repo: this.repoName,
        branch: branchName,
      }
    }).subscribe(() => {
      this.branche_remove_success = `${this.selectedBranchToRemove} was deleted successfully`;
      this.getBranches(); // Refresh list
      this.closeRemoveBranchModal();
    }, err => {
      this.branche_remove_error = `Error deleting branch ${this.selectedBranchToRemove}`;
      this.closeRemoveBranchModal();
    });}
    commits: any[] = [];

    getBranchCommits(branchName: string) {
      this.http.get<any[]>('/api/github/branch-commits', {
        params: {
          owner: this.ownerName,
          repo: this.repoName,
          branch: branchName
        }
      }).subscribe(
        (data) => {
          this.commits = data;  // ✅ This is what your HTML uses
          console.log('Commits for', branchName, data);
        },
        (err) => {
          console.error('Error fetching commits:', err);
        }
      );
    }
    
  }    
  
  