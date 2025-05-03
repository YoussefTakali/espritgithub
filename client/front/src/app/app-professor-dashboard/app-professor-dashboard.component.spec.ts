import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppProfessorDashboardComponent } from './app-professor-dashboard.component';

describe('AppProfessorDashboardComponent', () => {
  let component: AppProfessorDashboardComponent;
  let fixture: ComponentFixture<AppProfessorDashboardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AppProfessorDashboardComponent]
    });
    fixture = TestBed.createComponent(AppProfessorDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
