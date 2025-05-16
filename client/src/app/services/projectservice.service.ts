import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProjectserviceService {

  constructor(private http: HttpClient) { }

  getProjectsByTeacher(): Observable<any> {
    const userId = localStorage.getItem('id');
    return this.http.get<any>(`http://localhost:8080/api/projects/by-teacher/${userId}`)
      .pipe(
        tap(response => {
          console.log('Response inside service:', response);
        })
      );
  }
}
