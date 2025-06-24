import { Injectable } from '@angular/core';
 
@Injectable({
  providedIn: 'root'
})
export class GithubTokenService {
  private readonly token = 'kR'; 
 
  constructor() { }
 
  getToken(): string {
    return this.token;
  }
}