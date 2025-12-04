import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { ContainerService } from '../../services/container.service';
import { PickUpPointService } from '../../services/pickup-point.service';
import { VehiculeService } from '../../services/vehicule.service';
import { IncidentService } from '../../services/incident.service';
import { NotificationService } from '../../services/notification.service';
import { RouteService } from '../../services/route.service';
import { MapComponent } from '../map/map.component';
import { AppResponse } from '../../models/AppResponse';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-resources',
  standalone: true,
  imports: [CommonModule, MapComponent],
  templateUrl: './resources.component.html',
  styleUrl: './resources.component.css',
})
export class ResourcesComponent implements OnInit {
  @ViewChild(MapComponent) mapComponent!: MapComponent;

  activeTab: 'users' | 'containers' | 'pickuppoints' | 'vehicles' | 'incidents' | 'notifications' | 'routes' = 'users';

  users: any[] = [];
  containers: any[] = [];
  pickupPoints: any[] = [];
  vehicles: any[] = [];
  incidents: any[] = [];
  notifications: any[] = [];
  routes: any[] = [];

  loading: boolean = false;
  showMap: boolean = false; // Closed by default

  constructor(
    private userService: UserService,
    private containerService: ContainerService,
    private pickupPointService: PickUpPointService,
    private vehiculeService: VehiculeService,
    private incidentService: IncidentService,
    private notificationService: NotificationService,
    private routeService: RouteService,
    private toastService: ToastService
  ) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  setActiveTab(tab: 'users' | 'containers' | 'pickuppoints' | 'vehicles' | 'incidents' | 'notifications' | 'routes') {
    this.activeTab = tab;
    if (tab === 'users' && this.users.length === 0) {
      this.loadUsers();
    } else if (tab === 'containers' && this.containers.length === 0) {
      this.loadContainers();
    } else if (tab === 'pickuppoints' && this.pickupPoints.length === 0) {
      this.loadPickupPoints();
    } else if (tab === 'vehicles' && this.vehicles.length === 0) {
      this.loadVehicles();
    } else if (tab === 'incidents' && this.incidents.length === 0) {
      this.loadIncidents();
    } else if (tab === 'notifications' && this.notifications.length === 0) {
      this.loadNotifications();
    } else if (tab === 'routes' && this.routes.length === 0) {
      this.loadRoutes();
    }
  }

  toggleMap() {
    this.showMap = !this.showMap;
  }

  loadUsers() {
    this.loading = true;
    this.userService.getAll().subscribe({
      next: (res: AppResponse) => {
        this.users = (res as any).users || [];

        this.loading = false;
      },
      error: (err: any) => {
        this.toastService.showError(`${err.error.message}`);
        this.loading = false;
      }
    });
  }

  loadContainers() {
    this.loading = true;
    this.containerService.getAll().subscribe({
      next: (res: AppResponse) => {
        this.containers = (res as any).containers || [];
        this.loading = false;
      },
      error: (err: any) => {
        this.toastService.showError(`${err.error?.message || 'Failed to load containers'}`);
        this.loading = false;
      }
    });
  }

  loadPickupPoints() {
    this.loading = true;
    this.pickupPointService.getAll().subscribe({
      next: (res: AppResponse) => {
        this.pickupPoints = (res as any).pickuppoints || [];
        this.loading = false;
      },
      error: (err: any) => {
        this.toastService.showError(`${err.error?.message || 'Failed to load pickup points'}`);
        this.loading = false;
      }
    });
  }

  loadVehicles() {
    this.loading = true;
    this.vehiculeService.getAll().subscribe({
      next: (res: AppResponse) => {
        this.vehicles = (res as any).vehicules || [];
        console.log('Vehicles loaded:', this.vehicles);
        this.loading = false;
      },
      error: (err: any) => {
        this.toastService.showError(`${err.error?.message || 'Failed to load vehicles'}`);
        this.loading = false;
      }
    });
  }

  loadIncidents() {
    this.loading = true;
    this.incidentService.getAll().subscribe({
      next: (res: AppResponse) => {
        this.incidents = (res as any).incidents || [];
        this.loading = false;
      },
      error: (err: any) => {
        this.toastService.showError(`${err.error?.message || 'Failed to load incidents'}`);
        this.loading = false;
      }
    });
  }

  loadNotifications() {
    this.loading = true;
    this.notificationService.getAll().subscribe({
      next: (res: AppResponse) => {
        this.notifications = (res as any).notifications || [];
        this.loading = false;
      },
      error: (err: any) => {
        this.toastService.showError(`${err.error?.message || 'Failed to load notifications'}`);
        this.loading = false;
      }
    });
  }

  loadRoutes() {
    this.loading = true;
    this.routeService.getAll().subscribe({
      next: (res: AppResponse) => {
        this.routes = (res as any).routes || [];
        this.loading = false;
      },
      error: (err: any) => {
        this.toastService.showError(`${err.error?.message || 'Failed to load routes'}`);
        this.loading = false;
      }
    });
  }

  // Create route from optimized map data
  createRouteFromMap() {
    if (this.mapComponent && this.mapComponent.hasOptimizedRoute()) {
      this.mapComponent.saveOptimizedRoute();
      // Reload routes after a short delay to show the new route
      setTimeout(() => this.loadRoutes(), 1000);
    } else {
      this.toastService.showError('Please optimize a route first on the map');
    }
  }

  // Check if map has optimized route ready
  hasOptimizedRoute(): boolean {
    return this.mapComponent?.hasOptimizedRoute() || false;
  }
}
