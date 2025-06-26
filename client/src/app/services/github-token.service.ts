import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GithubTokenService {
  a="github_pat_11A7AWYQI0JZvaQlwMItRQ_vHeatRwPHtdeVP0wu8";
  private readonly token = 'iTkVq5B1LrjpQyB2S4I5uer8FMRKGVDRA8RUkGokR';  // Replace with your actual token

  constructor() { }

  getToken(): string {
    return this.token;
  }
}
