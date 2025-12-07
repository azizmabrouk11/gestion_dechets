import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({
    providedIn: 'root'
})
export class ToastService {

    constructor(private toastr: ToastrService) { }

    private getCommonConfig() {
        return {
            positionClass: 'toast-top-right',
            closeButton: true,
            progressBar: true,
            enableHtml: true,
            timeOut: 4000,
            extendedTimeOut: 1000,
            progressAnimation: 'decreasing' as const,
            messageClass: 'toast-message',
            titleClass: 'toast-title'
        };
    }

    // Success Messages
    showSuccess(message: string, title?: string, timeOut: number = 4000) {
        this.toastr.success(
            message,
            title || '✓ Success',
            { ...this.getCommonConfig(), timeOut }
        );
    }

    // Error Messages  
    showError(message: string, title?: string, timeOut: number = 5000) {
        this.toastr.error(
            message,
            title || '✕ Error',
            { ...this.getCommonConfig(), timeOut }
        );
    }

    // Warning Messages
    showWarning(message: string, title?: string, timeOut: number = 4500) {
        this.toastr.warning(
            message,
            title || '⚠ Warning',
            { ...this.getCommonConfig(), timeOut }
        );
    }

    showAccesDenied(message: string, title?: string, timeOut: number = 4500) {
        this.toastr.warning(
            `${message}, please contact the support team`,
            title || '⊘ Access Denied',
            { ...this.getCommonConfig(), timeOut }
        );
    }

    // Info Messages
    showInfo(message: string, title?: string, timeOut: number = 4000) {
        this.toastr.info(
            message,
            title || 'ⓘ Information',
            { ...this.getCommonConfig(), timeOut }
        );
    }

    // Specific Hotel Messages
    hotelCreated(hotelName: string) {
        this.showSuccess(`L'hôtel "${hotelName}" a été créé avec succès!`, 'Hôtel Ajouté');
    }

    hotelUpdated(hotelName: string) {
        this.showSuccess(`L'hôtel "${hotelName}" a été mis à jour avec succès!`, 'Hôtel Modifié');
    }

    hotelDeleted(hotelName: string) {
        this.showWarning(`L'hôtel "${hotelName}" a été supprimé.`, 'Hôtel Supprimé');
    }

    validationError(fieldName?: string) {
        const message = fieldName
            ? `field "${fieldName}" required.`
            : 'Veuillez remplir tous les champs obligatoires du formulaire.';
        this.showError(message, 'Erreur de Validation');
    }

    serverError() {
        this.showError('Une erreur serveur s\'est produite. Veuillez réessayer.', 'Erreur Serveur');
    }

    networkError() {
        this.showError('Problème de connexion. Vérifiez votre connexion internet.', 'Erreur Réseau');
    }
}
