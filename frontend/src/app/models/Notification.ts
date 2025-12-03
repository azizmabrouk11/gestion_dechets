import { NotificationType } from './enums/NotificationType';

export interface Notification {
    id?: string;
    subject: string;
    recipient: string;
    body: string;
    notificationType: NotificationType;
    userId?: string;
    containerId?: string;
    incidentId?: string;
}
