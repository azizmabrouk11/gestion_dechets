import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { CitizenComponent } from './pages/citizen/citizen.component';
import { BlankComponent } from './layout/blank/blank.component';
import { AppComponent } from './app.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { AuthGuard } from './keycloak/auth.guard';
// import { FaceLoginComponent } from './pages/face-login/face-login.component';
import { ProfileComponent } from './components/profile/profile.component';
import { ResourcesComponent } from './components/resources/resources.component';
import { MapComponent } from './components/map/map.component';

export const routes: Routes = [
    {
        path: '',
        component: MainLayoutComponent,
        children: [
            { path: '', component: HomeComponent },
            { path: 'home', component: HomeComponent }, // Add explicit home route,
            { path: 'dashboard', component: DashboardComponent, data: { roles: ['admin'] }, canActivate: [AuthGuard] },
            { path: 'Resources', component: ResourcesComponent, data: { roles: ['admin'] }, canActivate: [AuthGuard] },
            { path: 'map', component: MapComponent, data: { roles: ['admin', 'employe'] }, canActivate: [AuthGuard] },
            { path: 'profile', component: ProfileComponent, data: { roles: ['user'] } }
        ]
    },
    {
        path: 'blank',
        component: BlankComponent,
        children: [
            { path: 'blank', component: BlankComponent },

        ]
    },
];
