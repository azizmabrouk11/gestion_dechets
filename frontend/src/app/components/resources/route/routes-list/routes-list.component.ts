import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouteCardComponent } from '../route-card/route-card.component';

@Component({
    selector: 'app-routes-list',
    standalone: true,
    imports: [CommonModule, RouteCardComponent],
    templateUrl: './routes-list.component.html',

})
export class RoutesListComponent {
    @Input() routes: any[] = [];
    @Output() onViewRoute = new EventEmitter<any>();
    @Output() onDeleteRoute = new EventEmitter<any>();

    viewRoute(route: any) {
        this.onViewRoute.emit(route);
    }

    deleteRoute(route: any) {
        this.onDeleteRoute.emit(route);
    }
}
