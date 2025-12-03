import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PickUpPointService } from '../../services/pickup-point.service';
import { ContainerService } from '../../services/container.service';
import { RouteService } from '../../services/route.service';
import { PickUpPoint } from '../../models/PickUpPoint';
import { Container } from '../../models/Container';
import { Route } from '../../models/Route';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-citizen',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './citizen.component.html',
    styleUrl: './citizen.component.css'
})
export class CitizenComponent implements OnInit, OnDestroy {
    pickuppoints: PickUpPoint[] = [];
    containers: Container[] = [];
    upcomingRoutes: Route[] = [];

    isLoading = false;
    errorMessage = '';

    private subscriptions: Subscription[] = [];

    constructor(
        private pickUpPointService: PickUpPointService,
        private containerService: ContainerService,
        private routeService: RouteService
    ) { }

    ngOnInit(): void {
        this.loadData();
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(sub => sub.unsubscribe());
    }

    loadData(): void {
        this.isLoading = true;
        this.errorMessage = '';

        // Load pickup points (which include embedded containers)
        const pickUpSub = this.pickUpPointService.getAll().subscribe({
            next: (response) => {
                if (response.pickuppoints) {
                    this.pickuppoints = response.pickuppoints;
                    // Extract all containers from pickup points
                    this.containers = [];
                    this.pickuppoints.forEach(point => {
                        if (point.containers && point.containers.length > 0) {
                            this.containers.push(...point.containers);
                        }
                    });
                }
            },
            error: (error) => {
                console.error('Error loading pickup points:', error);
                this.errorMessage = 'Erreur lors du chargement des points de collecte';
            }
        });

        // Load upcoming routes
        const routeSub = this.routeService.getAll().subscribe({
            next: (response) => {
                if (response.routes) {
                    this.upcomingRoutes = response.routes.slice(0, 5);
                }
                this.isLoading = false;
            },
            error: (error) => {
                console.error('Error loading routes:', error);
                this.errorMessage = 'Erreur lors du chargement des données';
                this.isLoading = false;
            }
        });

        this.subscriptions.push(pickUpSub, routeSub);
    }

    getContainerStatusClass(status: string): string {
        return `status-${status.toLowerCase().replace('_', '-')}`;
    }

    getContainerTypeIcon(type: string): string {
        const icons: { [key: string]: string } = {
            'Plastique': '♻️',
            'Carton': '📦'
        };
        return icons[type] || '🗑️';
    }

    getContainersForPickUpPoint(pickUpPointId: string): Container[] {
        const point = this.pickuppoints.find(p => p.id === pickUpPointId);
        return point?.containers || [];
    }
}
