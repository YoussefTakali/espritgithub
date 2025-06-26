import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'client';

  constructor(private keycloakService: KeycloakService, private http: HttpClient) {}

  async ngOnInit() {
    try {
      const token = await this.keycloakService.getToken();
      localStorage.setItem('token', token);
      console.log(localStorage.getItem('teacher'));
      const profile = await this.keycloakService.loadUserProfile();
      const email = profile.email;
      const firstname = profile.firstName;
      const lastname = profile.lastName;
      const userid =profile.id
      const attributes: any = (profile as any).attributes;
      const githubUsername = attributes?.githubUsername?.[0];
      const githubToken = attributes?.githubToken?.[0];
      localStorage.setItem('firstname', firstname!);
      localStorage.setItem('lastname', lastname!);
      localStorage.setItem('id',userid!); 
      if (email) {
        localStorage.setItem('email', email);
      }

      if (githubUsername && githubToken) {
        localStorage.setItem('githubUsername', githubUsername);
        localStorage.setItem('githubToken', githubToken);

        // Vérification du token GitHub
        this.verifyGithubToken(githubToken);
      } else {
        console.warn('Attributs githubUsername ou githubToken manquants.');
      }
    } catch (err) {
      console.error('Erreur lors de la récupération du profil ou du token :', err);
    }
  }

 verifyGithubToken(githubToken: string) {
    const apiUrl = 'https://api.github.com/user/repos?visibility=private'; // Use the provided API endpoint

    // Create headers with the Authorization token
    const headers = new HttpHeaders({
      'Authorization': `token ${githubToken}` // Use 'token' for Personal Access Tokens
    });

    this.http.get(apiUrl, { headers: headers, observe: 'response' }).subscribe(
      response => {
        if (response.status === 200) {
          console.log('GitHub token verification successful. Status:', response.status);
        } else {
          console.warn('GitHub token verification failed. Status:', response.status);
          // Affichage de l'alerte et redirection vers Keycloak pour récupérer un nouveau token
          alert('Token GitHub invalide. Vous allez être redirigé vers la page de gestion de votre compte.');
          const keycloakAccountUrl = this.keycloakService.getKeycloakInstance().createAccountUrl();
          window.location.href = keycloakAccountUrl;  // Redirection vers la page de gestion du compte Keycloak
        }
      },
      error => {
        console.error('Error verifying GitHub token:', error);
        // Affichage de l'alerte en cas d'erreur
        alert('Erreur lors de la vérification du token GitHub. Vous allez être redirigé vers la page de gestion de votre compte.');
        const keycloakAccountUrl = this.keycloakService.getKeycloakInstance().createAccountUrl();
        window.location.href = keycloakAccountUrl;  // Redirection vers la page de gestion du compte Keycloak
      }
    );
  }
}