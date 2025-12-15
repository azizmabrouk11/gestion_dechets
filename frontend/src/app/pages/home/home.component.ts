import { Component, OnInit, AfterViewInit, OnDestroy, Renderer2, ChangeDetectorRef } from "@angular/core";
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subscription, forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AppKeycloakService } from "../../keycloak/appKeycloakService";
import { UserRole } from "../../models/enums/UserRole";
import { PickUpPointService } from '../../services/pickup-point.service';
import { ContainerService } from '../../services/container.service';
import { VehiculeService } from '../../services/vehicule.service';
import { RouteService } from '../../services/route.service';
import { UserService } from '../../services/user.service';
import { IncidentService } from '../../services/incident.service';
import { AppResponse } from '../../models/AppResponse';

interface DashboardStats {
    pickUpPointsCount: number;
    containersCount: number;
    fullContainersCount: number;
    nearFullContainersCount: number;
    vehiculesCount: number;
    activeVehiclesCount: number;
    routesCount: number;
    usersCount: number;
    employeesAssignedTodayCount: number;
    incidentsCount: number;
    averageFillPercent: number;
}

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
    // IntersectionObserver for scroll indicator
    private intersectionObserver?: IntersectionObserver;
    private markerCount = 5; // Start + 4 sections
    // Authentication state
    isAuthenticated = false;
    isAdmin = false;
    profileRole?: string | null = null;

    // Dashboard statistics
    stats: DashboardStats = {
        pickUpPointsCount: 0,
        containersCount: 0,
        fullContainersCount: 0,
        nearFullContainersCount: 0,
        vehiculesCount: 0,
        activeVehiclesCount: 0,
        routesCount: 0,
        usersCount: 0,
        employeesAssignedTodayCount: 0,
        incidentsCount: 0,
        averageFillPercent: 0
    };

    // Loading state
    isLoadingStats = true;

    // Subscriptions
    private subscriptions = new Subscription();

    // Constants
    private readonly FULL_CONTAINER_THRESHOLD = 0.8;
    private readonly NEAR_FULL_CONTAINER_THRESHOLD = 0.6;
    private readonly ACTIVE_VEHICLE_STATUS = 'Fonctionnel';
    private readonly EMPLOYEE_ROLE = 'Employe';

    constructor(
        private appKeycloakService: AppKeycloakService,
        private pickUpPointService: PickUpPointService,
        private containerService: ContainerService,
        private vehiculeService: VehiculeService,
        private routeService: RouteService,
        private userService: UserService,
        private incidentService: IncidentService
        , private renderer: Renderer2
        , private cd: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.initializeAuthentication();
        this.loadDashboardStats();
    }

    ngAfterViewInit(): void {
        // Observe the hero and each content section to update the scroll indicator
        const elements = Array.from(document.querySelectorAll('.hero, .content-section')) as Element[];

        if (elements.length === 0) {
            return;
        }

        this.intersectionObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const index = elements.indexOf(entry.target as Element);
                    // Clamp index to marker range (0..markerCount-1)
                    const clamped = Math.max(0, Math.min(this.markerCount - 1, index));
                    this.updateScrollIndicator(clamped);
                }
            });
        }, { threshold: 0.5 });

        elements.forEach(el => this.intersectionObserver!.observe(el));

        // Initial update — try to determine which section is currently visible
        // (observer callbacks may run asynchronously, so set Start as default)
        this.updateScrollIndicator(0);
    }

    ngOnDestroy(): void {
        this.subscriptions.unsubscribe();
        if (this.intersectionObserver) {
            this.intersectionObserver.disconnect();
        }
    }

    /** Update the scroll indicator markers and progress bar */
    private updateScrollIndicator(activeIndex: number): void {
        const spans = Array.from(document.querySelectorAll('.scroll-numbers span')) as HTMLElement[];
        const progressEl = document.querySelector('.scroll-progress') as HTMLElement | null;

        if (!spans || spans.length === 0) {
            return;
        }

        spans.forEach((s, i) => {
            if (i === activeIndex) {
                s.classList.remove('inactive');
            } else {
                s.classList.add('inactive');
            }
        });

        if (progressEl) {
            const percent = (activeIndex / (spans.length - 1));
            // use transform scaleY for smoother GPU-accelerated animation
            this.renderer.setStyle(progressEl, 'transform', `scaleY(${percent})`);
        }

        // trigger change detection in case template bindings depend on this
        try { this.cd.detectChanges(); } catch { /* ignore */ }
    }

    /**
     * Initialize authentication state and subscribe to profile changes
     */
    private initializeAuthentication(): void {
        this.isAuthenticated = this.appKeycloakService.isLoggedIn();

        const profileSub = this.appKeycloakService.profileObservable.subscribe(profile => {
            this.isAdmin = profile?.role === UserRole.admin;
            this.profileRole = profile?.role ? profile.role.toString() : null;
        });

        this.subscriptions.add(profileSub);
    }

    /**
     * Navigate to appropriate dashboard based on user role
     */
    navigateToDashboard(): void {
        const route = this.isAdmin ? '/dashboard' : '/citizen';
        window.location.href = route;
    }

    /**
     * Trigger Keycloak login
     */
    login(): void {
        this.appKeycloakService.login();
    }

    /**
     * Load all dashboard statistics in parallel
     */
    private loadDashboardStats(): void {
        this.isLoadingStats = true;

        const stats$ = forkJoin({
            pickupPoints: this.pickUpPointService.getAll(),
            containers: this.containerService.getAll(),
            vehicules: this.vehiculeService.getAll(),
            routes: this.routeService.getAll(),
            users: this.userService.getAll(),
            incidents: this.incidentService.getAll()
        }).pipe(
            finalize(() => this.isLoadingStats = false)
        );

        const statsSub = stats$.subscribe({
            next: (responses) => {
                this.processPickupPointsData(responses.pickupPoints);
                this.processContainersData(responses.containers);
                this.processVehiculesData(responses.vehicules);
                this.processRoutesData(responses.routes);
                this.processUsersData(responses.users);
                this.processIncidentsData(responses.incidents);
            },
            error: (error) => {
                console.error('Error loading dashboard statistics:', error);
                this.resetStats();
            }
        });

        this.subscriptions.add(statsSub);
    }

    /**
     * Process pickup points data
     */
    private processPickupPointsData(response: AppResponse): void {
        this.stats.pickUpPointsCount = response.pickuppoints?.length ?? 0;
    }

    /**
     * Process containers data and calculate fill statistics
     */
    private processContainersData(response: AppResponse): void {
        const containers = response.containers ?? [];
        this.stats.containersCount = containers.length;

        if (containers.length === 0) {
            return;
        }

        let totalFillLevel = 0;
        let totalCapacity = 0;
        let fullContainers = 0;
        let nearFullContainers = 0;

        containers.forEach(container => {
            const fillRatio = this.calculateFillRatio(container.fillLevel, container.capacity);

            totalFillLevel += container.fillLevel;
            totalCapacity += container.capacity;

            if (fillRatio >= this.FULL_CONTAINER_THRESHOLD) {
                fullContainers++;
            } else if (fillRatio >= this.NEAR_FULL_CONTAINER_THRESHOLD) {
                nearFullContainers++;
            }
        });

        this.stats.fullContainersCount = fullContainers;
        this.stats.nearFullContainersCount = nearFullContainers;
        this.stats.averageFillPercent = this.calculateAverageFillPercent(totalFillLevel, totalCapacity);
    }

    /**
     * Calculate fill ratio for a container
     */
    private calculateFillRatio(fillLevel: number, capacity: number): number {
        return capacity > 0 ? fillLevel / capacity : 0;
    }

    /**
     * Calculate average fill percentage
     */
    private calculateAverageFillPercent(totalFillLevel: number, totalCapacity: number): number {
        if (totalCapacity === 0) {
            return 0;
        }
        return Math.round((totalFillLevel / totalCapacity) * 100);
    }

    /**
     * Process vehicles data
     */
    private processVehiculesData(response: AppResponse): void {
        const vehicules = response.vehicules ?? [];
        this.stats.vehiculesCount = vehicules.length;
        this.stats.activeVehiclesCount = vehicules.filter(
            v => (v as any).status === this.ACTIVE_VEHICLE_STATUS
        ).length;
    }

    /**
     * Process routes data
     */
    private processRoutesData(response: AppResponse): void {
        this.stats.routesCount = response.routes?.length ?? 0;
    }

    /**
     * Process users data
     */
    private processUsersData(response: AppResponse): void {
        const users = response.users ?? [];
        this.stats.usersCount = users.length;
        this.stats.employeesAssignedTodayCount = users.filter(
            u => (u as any).role === this.EMPLOYEE_ROLE && (u as any).isActive
        ).length;
    }

    /**
     * Process incidents data
     */
    private processIncidentsData(response: AppResponse): void {
        this.stats.incidentsCount = response.incidents?.length ?? 0;
    }

    /**
     * Reset all statistics to zero
     */
    private resetStats(): void {
        this.stats = {
            pickUpPointsCount: 0,
            containersCount: 0,
            fullContainersCount: 0,
            nearFullContainersCount: 0,
            vehiculesCount: 0,
            activeVehiclesCount: 0,
            routesCount: 0,
            usersCount: 0,
            employeesAssignedTodayCount: 0,
            incidentsCount: 0,
            averageFillPercent: 0
        };
    }

    // Getters for template access
    get pickUpPointsCount(): number { return this.stats.pickUpPointsCount; }
    get containersCount(): number { return this.stats.containersCount; }
    get fullContainersCount(): number { return this.stats.fullContainersCount; }
    get nearFullContainersCount(): number { return this.stats.nearFullContainersCount; }
    get vehiculesCount(): number { return this.stats.vehiculesCount; }
    get activeVehiclesCount(): number { return this.stats.activeVehiclesCount; }
    get routesCount(): number { return this.stats.routesCount; }
    get usersCount(): number { return this.stats.usersCount; }
    get employeesAssignedTodayCount(): number { return this.stats.employeesAssignedTodayCount; }
    get incidentsCount(): number { return this.stats.incidentsCount; }
    get averageFillPercent(): number { return this.stats.averageFillPercent; }
}