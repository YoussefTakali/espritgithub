import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { AuthGuard } from './keycloak/app.guard';

const routes: Routes = [
  {
    path: '',
    component:MainLayoutComponent,
    canActivate: [AuthGuard], // ✅ protection par Keycloak

    children:[
      {path:'', redirectTo:'dashboard',pathMatch:'full'},
      { path:'dashboard',component:DashboardComponent},
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
