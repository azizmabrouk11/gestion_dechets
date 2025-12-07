import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationCardComponent } from '../notification-card/notification-card.component';

@Component({
    selector: 'app-notifications-list',
    standalone: true,
    imports: [CommonModule, NotificationCardComponent],
    templateUrl: './notifications-list.component.html',

})
export class NotificationsListComponent {
    @Input() notifications: any[] = [];
    @Output() onViewNotification = new EventEmitter<any>();
    @Output() onDeleteNotification = new EventEmitter<any>();

    viewNotification(notification: any) {
        this.onViewNotification.emit(notification);
    }

    deleteNotification(notification: any) {
        this.onDeleteNotification.emit(notification);
    }
}
