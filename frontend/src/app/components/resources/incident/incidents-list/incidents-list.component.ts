import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IncidentCardComponent } from '../incident-card/incident-card.component';

@Component({
    selector: 'app-incidents-list',
    standalone: true,
    imports: [CommonModule, IncidentCardComponent],
    templateUrl: './incidents-list.component.html',
  
})
export class IncidentsListComponent {
    @Input() incidents: any[] = [];
    @Output() onViewIncident = new EventEmitter<any>();
    @Output() onResolveIncident = new EventEmitter<any>();

    viewIncident(incident: any) {
        this.onViewIncident.emit(incident);
    }

    resolveIncident(incident: any) {
        this.onResolveIncident.emit(incident);
    }
}
