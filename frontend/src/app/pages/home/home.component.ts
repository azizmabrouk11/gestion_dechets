import { Component, OnInit, OnDestroy } from "@angular/core";
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AppKeycloakService } from "../../keycloak/appKeycloakService";
import { Subscription } from 'rxjs';
import { UserRole } from "../../models/enums/UserRole";

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './home.component.html',
    styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit, OnDestroy {
    admin = false;
    isAuthenticated = false;
    private profileSubscription?: Subscription;

    constructor(private AppkeyCloakService: AppKeycloakService) { }

    ngOnInit(): void {
        this.isAuthenticated = this.AppkeyCloakService.isLoggedIn();
        this.profileSubscription = this.AppkeyCloakService.profileObservable.subscribe(profile => {
            this.admin = profile && profile.role === UserRole.Admin ? true : false;
        });
    }

    ngOnDestroy(): void {
        if (this.profileSubscription) {
            this.profileSubscription.unsubscribe();
        }
    }

    get isAdmin(): boolean {
        const currentProfile = this.AppkeyCloakService.profile;
        return currentProfile && currentProfile.role === UserRole.Admin ? true : false;
    }

    navigateToDashboard(): void {
        if (this.isAdmin) {
            window.location.href = '/dashboard';
        } else {
            window.location.href = '/citizen';
        }
    }
}