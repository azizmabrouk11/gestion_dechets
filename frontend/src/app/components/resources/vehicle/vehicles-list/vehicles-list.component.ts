import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VehicleCardComponent } from '../vehicle-card/vehicle-card.component';

@Component({
    selector: 'app-vehicles-list',
    standalone: true,
    imports: [CommonModule, VehicleCardComponent],
    templateUrl: './vehicles-list.component.html',
  
})
export class VehiclesListComponent {
    @Input() vehicles: any[] = [];
    @Output() onEditVehicle = new EventEmitter<any>();
    @Output() onDeleteVehicle = new EventEmitter<any>();

    editVehicle(vehicle: any) {
        this.onEditVehicle.emit(vehicle);
    }

    deleteVehicle(vehicle: any) {
        this.onDeleteVehicle.emit(vehicle);
    }
}
