import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { VehiculeService } from '../../services/vehicule.service';
import { ContainerService } from '../../services/container.service';
import { RouteService } from '../../services/route.service';
import { PickUpPointService } from '../../services/pickup-point.service';
import { UserProfile } from '../../models/UserProfile';
import { Vehicule } from '../../models/Vehicule';
import { Container } from '../../models/Container';
import { Route } from '../../models/Route';
import { PickUpPoint } from '../../models/PickUpPoint';
import { Subscription } from 'rxjs';
import { AppResponse } from '../../models/AppResponse';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit, OnDestroy {
  users: UserProfile[] = [];
  vehicules: Vehicule[] = [];
  containers: Container[] = [];
  routes: Route[] = [];
  pickuppoints: PickUpPoint[] = [];

  isLoading = true;
  errorMessage = '';
  activeSection = 'overview';

  private subscriptions: Subscription[] = [];

  constructor(
    private userService: UserService,
    private vehiculeService: VehiculeService,
    private containerService: ContainerService,
    private routeService: RouteService,
    private pickUpPointService: PickUpPointService
  ) { }

  ngOnInit(): void {
    this.loadAllData();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  loadAllData(): void {
    this.loadUsers();
    this.loadVehicules();
    this.loadContainers();
    this.loadRoutes();
    this.loadPickUpPoints();
  }

  loadUsers(): void {
    const sub = this.userService.getAll().subscribe({
      next: (response) => {
        if (response.status === 200 && response.users) {
          this.users = response.users;
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading users:', error);
        this.errorMessage = error?.status === 403
          ? 'Access denied. Admin role required.'
          : 'Failed to load users. Please try again.';
        this.isLoading = false;
      }
    });
    this.subscriptions.push(sub);
  }

  loadVehicules(): void {
    const sub = this.vehiculeService.getAll().subscribe({
      next: (response) => {
        if (response.vehicules) {
          this.vehicules = response.vehicules;
        }
      },
      error: (error) => console.error('Error loading vehicules:', error)
    });
    this.subscriptions.push(sub);
  }

  loadContainers(): void {
    const sub = this.containerService.getAll().subscribe({
      next: (response) => {
        if (response.containers) {
          this.containers = response.containers;
        }
      },
      error: (error) => console.error('Error loading containers:', error)
    });
    this.subscriptions.push(sub);
  }

  loadRoutes(): void {
    const sub = this.routeService.getAll().subscribe({
      next: (response) => {
        if (response.routes) {
          this.routes = response.routes;
        }
      },
      error: (error) => console.error('Error loading routes:', error)
    });
    this.subscriptions.push(sub);
  }

  loadPickUpPoints(): void {
    const sub = this.pickUpPointService.getAll().subscribe({
      next: (response: AppResponse) => {
        if (response.pickuppoints) {
          this.pickuppoints = response.pickuppoints;
        }
      },
      error: (error) => console.error('Error loading pickup points:', error)
    });
    this.subscriptions.push(sub);
  }

  get activeUsersCount(): number {
    return this.users.filter(u => u.isActive).length;
  }

  get availableVehiculesCount(): number {
    return this.vehicules.filter(v => v.vehiculeStatus === 'functionel').length;
  }

  get functionalContainersCount(): number {
    return this.containers.filter(c => c.containerStatus === 'functionel').length;
  }

  get todayRoutesCount(): number {
    const today = new Date().toISOString().split('T')[0];
    return this.routes.filter(r => r.routeDate.startsWith(today)).length;
  }

  getRoleBadgeClass(role: string): string {
    return role === 'ADMIN' ? 'badge-admin' : 'badge-user';
  }

  getVehiculeStatusClass(status: string): string {
    return status === 'fonctionnel' ? 'status-available' : 'status-maintenance';
  }

  getContainerStatusClass(status: string): string {
    return status === 'functional' ? 'status-functional' : 'status-non-functional';
  }

  setActiveSection(section: string): void {
    this.activeSection = section;
  }
}
