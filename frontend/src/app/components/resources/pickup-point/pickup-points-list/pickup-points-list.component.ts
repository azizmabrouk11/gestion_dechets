import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PickupPointCardComponent } from '../pickup-point-card/pickup-point-card.component';

@Component({
    selector: 'app-pickup-points-list',
    standalone: true,
    imports: [CommonModule, PickupPointCardComponent],
    templateUrl: './pickup-points-list.component.html',
   
})
export class PickupPointsListComponent {
    @Input() pickupPoints: any[] = [];
    @Output() onEditPickupPoint = new EventEmitter<any>();
    @Output() onDeletePickupPoint = new EventEmitter<any>();

    editPickupPoint(point: any) {
        this.onEditPickupPoint.emit(point);
    }

    deletePickupPoint(point: any) {
        this.onDeletePickupPoint.emit(point);
    }
}
