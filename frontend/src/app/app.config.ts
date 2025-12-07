import { ApplicationConfig, provideZoneChangeDetection, importProvidersFrom, APP_INITIALIZER } from '@angular/core';
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
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }]
};
