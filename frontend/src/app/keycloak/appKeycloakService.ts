import { Injectable } from "@angular/core";
import { KeycloakService } from 'keycloak-angular';
import { UserProfile } from "../models/UserProfile";
import { UserService } from "../../app/services/user.service";
import { BehaviorSubject, Observable } from 'rxjs';
import { AppResponse } from "../../app/models/AppResponse";
import { UserRole } from "../../app/models/enums/UserRole";

/**
 * ==========================================
 * ENVIRONMENT CONFIGURATION
 * ==========================================
 * Angular automatically selects the correct environment file during build:
 * 
 * Development Build (ng build --configuration=development):
 *   - Uses: src/environments/environment.ts
 *   - Keycloak URL: http://localhost:8080
 *   - API URL: http://localhost:8083/api
 *   - Redirects: http://localhost:4200
 * 
 * Production Build (ng build --configuration=production):
 *   - Uses: src/environments/environment.prod.ts (replaces environment.ts)
 *   - Keycloak URL: https://app.46.lebondeveloppeur.net
 *   - API URL: https://app.46.lebondeveloppeur.net/api
 *   - Redirects: https://app.46.lebondeveloppeur.net
 * 
 * The import below always points to 'environment', but the actual file
 * used is determined by angular.json fileReplacements configuration.
 * 
 * To switch environments:
 *   1. Update .env: PROFILE=dev or PROFILE=prod
 *   2. Rebuild frontend: docker compose build --no-cache front
 *   3. Restart: docker compose up -d
 * 
 * See ENVIRONMENT_GUIDE.md for complete documentation.
 */
import { environment } from "../../environments/environment-dev";

@Injectable({
    providedIn: 'root'
})
export class AppKeycloakService {
    private _KeycloakService: KeycloakService;
    private _profile: Partial<UserProfile> | undefined;
    private _profileSubject = new BehaviorSubject<Partial<UserProfile> | undefined>(undefined);
    private _isInitialized = false;

    constructor(private keycloakService: KeycloakService, private userService: UserService) {
        this._KeycloakService = keycloakService;
    }

    // Check if Keycloak is initialized
    get isInitialized(): boolean {
        return this._isInitialized;
    }

    // Getter to access the keycloak instance
    get KeycloakService(): KeycloakService {
        return this._KeycloakService;
    }

    // Getter to access user profile
    get profile(): Partial<UserProfile> | undefined {
        return this._profile;
    }

    // Observable to track profile changes
    get profileObservable(): Observable<Partial<UserProfile> | undefined> {
        return this._profileSubject.asObservable();
    }

    async init(): Promise<boolean> {
        // Log current environment for debugging
        console.log('🔧 Keycloak Init - Environment:', {
            production: environment.production,
            keycloakUrl: environment.KEYCLOAK_URL,
            apiUrl: environment.API_URL,
            realm: environment.KEYCLOAK_REALM,
            clientId: environment.KEYCLOAK_CLIENT_ID
        });

        const auth = await this.keycloakService.init({
            config: {
                url: 'https://dechet.46.lebondeveloppeur.net',   // ← NO /auth !!
                realm: 'dechetrealm',
                clientId: 'dechet-frontend'
            },
            initOptions: {
                onLoad: 'check-sso',           // ← same as hotel app
                silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
                checkLoginIframe: false,
                pkceMethod: 'S256'
            },
            enableBearerInterceptor: true,    // THIS LINE IS THE MAGIC
            bearerExcludedUrls: ['/assets']   // optional – exclude only static files
        });

        if (auth) {
            console.log('✅ Keycloak authentication successful');
            await this.loadUserProfile();

            // Set up token refresh handler only for authenticated users
            this._KeycloakService.getKeycloakInstance().onTokenExpired = () => {

                this._KeycloakService.getKeycloakInstance().updateToken(30).then(refreshed => {
                    if (refreshed) {


                        this.loadUserProfile();
                    } else {

                        this.logout();
                    }
                }).catch(error => {

                    this.logout();
                });
            };
        } else {

        }

        this._isInitialized = true; // Mark as initialized
        return auth;
    }

    async loadUserProfile(): Promise<void> {
        try {
            // Get user information from the token instead of making API call
            const tokenParsed = this._KeycloakService.getKeycloakInstance().tokenParsed;
            const token = await this._KeycloakService.getToken();



            if (tokenParsed) {
                // Roles may live under realm_access or resource_access (client roles)
                const realmRoles: string[] = tokenParsed['realm_access']?.roles || [];
                const clientRoles: string[] = tokenParsed['resource_access']?.[environment.KEYCLOAK_CLIENT_ID]?.roles || [];
                const keycloakRoles: string[] = [...realmRoles, ...clientRoles];

                // Normalize and map roles
                const hasAdmin = keycloakRoles.some(r => (r || '').toString().toLowerCase() === 'admin');
                const hasEmploye = keycloakRoles.some(r => {
                    const v = (r || '').toString().toLowerCase();
                    return v === 'employe' || v === 'employee' || v === 'employé';
                });

                let userRole = UserRole.user;
                if (hasAdmin) {
                    userRole = UserRole.admin;
                } else if (hasEmploye) {
                    userRole = UserRole.employe;
                }

                console.log('Detected Keycloak roles:', JSON.stringify(keycloakRoles), 'mapped role:', userRole);

                this._profile = {
                    userName: tokenParsed['preferred_username'] || tokenParsed['sub'] || '',
                    email: tokenParsed['email'] || '',
                    firstName: tokenParsed['given_name'] || '',
                    lastName: tokenParsed['family_name'] || '',
                    token: token,
                    role: userRole,
                } as Partial<UserProfile>;

                if (this._profile?.email) {
                    // Use email as the primary and only identifier
                    this.syncUserByEmail(this._profile);
                } else {
                    // No email available, cannot proceed with user sync
                    console.warn('No email available for user, using Keycloak profile only:', this._profile);
                    this._profileSubject.next(this._profile);
                }


            } else {
                console.warn('No token parsed available');
            }
        } catch (error) {
            console.error('Failed to load user profile:', error);
        }
    }

    /**
     * Load profile for face-authenticated users
     */
    loadFaceAuthProfile(): void {
        const faceAuthUser = localStorage.getItem('faceAuthUser');
        const faceAuthToken = localStorage.getItem('access_token');
        const faceAuthEmail = localStorage.getItem('faceAuthEmail');
        const faceAuthFirstName = localStorage.getItem('faceAuthFirstName');
        const faceAuthLastName = localStorage.getItem('faceAuthLastName');
        const faceAuthProfileImage = localStorage.getItem('faceAuthProfileImage');
        const faceAuthEnabled = localStorage.getItem('faceAuthEnabled');

        if (faceAuthUser && faceAuthToken) {
            this._profile = {
                userName: faceAuthUser,
                email: faceAuthEmail || '',
                firstName: faceAuthFirstName || '',
                lastName: faceAuthLastName || '',
                profileImage: faceAuthProfileImage || '',
                faceAuthEnabled: faceAuthEnabled === 'true',
                token: faceAuthToken,
                role: UserRole.user, // Face authenticated users are USER role
            } as Partial<UserProfile>;

            console.log('Face auth profile loaded:', this._profile);
            this._profileSubject.next(this._profile);
        }
    }

    // Helper methods for authentication
    login(): void {
        this._KeycloakService.login({ redirectUri: window.location.href }); // ✅ Use current page URL
    }

    logout(): void {
        // Clear face authentication data
        const faceAuthToken = localStorage.getItem('access_token');
        if (faceAuthToken && faceAuthToken.startsWith('FACE_AUTH_TOKEN_')) {
            // Face authentication logout - clear all face auth data
            localStorage.removeItem('access_token');
            localStorage.removeItem('token_type');
            localStorage.removeItem('refresh_token');
            localStorage.removeItem('faceAuthUser');
            localStorage.removeItem('faceAuthEmail');
            localStorage.removeItem('faceAuthFirstName');
            localStorage.removeItem('faceAuthLastName');
            localStorage.removeItem('faceAuthProfileImage');
            localStorage.removeItem('faceAuthEnabled');

            // Clear profile
            this._profile = undefined;
            this._profileSubject.next(undefined);

            // Redirect to home page
            window.location.href = window.location.origin;
        } else {
            // Keycloak logout
            this._KeycloakService.logout(window.location.origin);
        }
    }
    signUp() {
        this._KeycloakService.register({ redirectUri: window.location.href }); // ✅ Use current page URL
    }

    isLoggedIn(): boolean {
        // Check for face authentication first
        const faceAuthToken = localStorage.getItem('access_token');
        const faceAuthUser = localStorage.getItem('faceAuthUser');
        if (faceAuthToken && faceAuthUser && faceAuthToken.startsWith('FACE_AUTH_TOKEN_')) {
            // Load face auth profile if not already loaded
            if (!this._profile || this._profile.userName !== faceAuthUser) {
                this.loadFaceAuthProfile();
            }
            return true;
        }

        return this._KeycloakService.isLoggedIn();
    }

    getToken(): Promise<string> {
        return this._KeycloakService.getToken();
    }

    hasRealmRole(role: string): boolean {
        return this._KeycloakService.isUserInRole(role);
    }

    hasResourceRole(role: string, resource?: string): boolean {
        return this._KeycloakService.isUserInRole(role, resource);
    }

    /**
     * Updates the local profile data and notifies subscribers
     * @param updatedProfile The updated profile data from backend
     */
    updateLocalProfile(updatedProfile: Partial<UserProfile>): void {
        // Preserve the role from Keycloak when updating with backend data
        const originalRole = this._profile?.role;
        this._profile = { ...this._profile, ...updatedProfile, role: originalRole };
        this._profileSubject.next(this._profile);
        console.log('Local profile updated with preserved role:', this._profile);
    }

    /**
     * Simple email-based sync logic
     * Let the backend handle all the logic (check if exists, create or update)
     * @param profile The user profile to sync
     */
    private syncUserByEmail(profile: Partial<UserProfile>): void {
        if (!profile.email) {
            console.warn('Cannot sync user without email');
            this._profileSubject.next(profile);
            return;
        }

        // Just call createOrUpdateUser - let backend handle everything
        console.log('Syncing user with email:', profile.email);
        this.userService.createOrUpdateUser(profile).subscribe({
            next: (response: AppResponse) => {
                // Preserve the role from Keycloak token when updating with backend data
                const originalRole = this._profile?.role;
                this._profile = { ...response?.user, role: originalRole };
                this._profileSubject.next(this._profile);
                console.log('Profile synced with preserved role:', this._profile);
            },
            error: (error: any) => {
                console.error('Failed to sync user:', error);
                // Still use Keycloak profile even if database sync fails
                this._profileSubject.next(this._profile);
            }
        });
    }


}