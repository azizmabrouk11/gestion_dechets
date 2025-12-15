import { ApplicationConfig, provideZoneChangeDetection, importProvidersFrom, APP_INITIALIZER } from '@angular/core';
import { Router } from '@angular/router';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';

import { provideHttpClient, withInterceptors, HTTP_INTERCEPTORS, withInterceptorsFromDi } from '@angular/common/http';
import { AppKeycloakService } from './keycloak/appKeycloakService';
import { AuthInterceptor } from './keycloak/auth.interceptor';

import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { ToastrModule } from 'ngx-toastr';


export function initializekeycloak(appKeycloakService: AppKeycloakService) {
  return () => {
    return appKeycloakService.init();
  };
}
export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(ToastrModule.forRoot({
      timeOut: 4000,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
      closeButton: true,
      progressBar: true,
      progressAnimation: 'decreasing' as const,
      newestOnTop: true,
      tapToDismiss: true,
      maxOpened: 3,
      autoDismiss: true
    })),


    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()), // This enables class-based interceptors
    provideAnimationsAsync(), // Required for Material components

    KeycloakService,
    {
      provide: APP_INITIALIZER,
      useFactory: initializekeycloak,
      deps: [AppKeycloakService],
      multi: true
    },
    // Prevent Angular router from throwing on silent-check-sso.html navigation errors
    {
      provide: APP_INITIALIZER,
      useFactory: (router: Router) => {
        return () => {
          // `errorHandler` is available at runtime but TypeScript's Router type
          // can be stricter in AOT builds; cast to `any` to avoid TS2339.
          (router as any).errorHandler = (error: any) => {
            try {
              const msg = error?.message || '';
              if (msg.includes('silent-check-sso.html') || msg.includes('silent-check-sso')) {
                console.warn('Ignored router navigation error for silent SSO:', msg);
                return null;
              }
            } catch (e) {
              // ignore
            }
            throw error;
          };
        };
      },
      deps: [Router],
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }]
};
