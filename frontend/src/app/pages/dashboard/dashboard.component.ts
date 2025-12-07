import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, KeyValuePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { VehiculeService } from '../../services/vehicule.service';
import { ContainerService } from '../../services/container.service';
import { RouteService } from '../../services/route.service';
import { PickUpPointService } from '../../services/pickup-point.service';
import { IncidentService } from '../../services/incident.service';
import { UserProfile } from '../../models/UserProfile';
import { Vehicule } from '../../models/Vehicule';
import { VehiculeStatus } from '../../models/enums/VehiculeStatus';
import { VehiculeType } from '../../models/enums/VehiculeType';
import { Container } from '../../models/Container';
import { Route } from '../../models/Route';
import { PickUpPoint } from '../../models/PickUpPoint';
import { Incident } from '../../models/Incident';
import { Subscription } from 'rxjs';
import { AppResponse } from '../../models/AppResponse';
import {
  NgApexchartsModule,
  ApexChart,
  ApexNonAxisChartSeries,
  ApexResponsive,
  ApexTitleSubtitle
} from "ng-apexcharts";


@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, FormsModule, NgApexchartsModule, KeyValuePipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit, OnDestroy {
  users: UserProfile[] = [];
  vehicules: Vehicule[] = [];
  containers: Container[] = [];
  routes: Route[] = [];
  pickuppoints: PickUpPoint[] = [];
  incidents: Incident[] = [];
  public containerStatusChart: any;
  public containerTypeChart: any;
  public containerFillChart: any;
  public vehicleStatusChart: any;
  public vehicleTypeChart: any;
  public incidentStatusChart: any;

  searchTerm = '';
  isLoading = true;
  errorMessage = '';
  activeSection = 'overview';

  private subscriptions: Subscription[] = [];

  constructor(
    private userService: UserService,
    private vehiculeService: VehiculeService,
    private containerService: ContainerService,
    private routeService: RouteService,
    private pickUpPointService: PickUpPointService,
    private incidentService: IncidentService
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
    this.loadIncidents();
  }

  loadUsers(): void {
    const sub = this.userService.getAll().subscribe({
      next: (response) => {
        if (response.status === 200 && response.users) {
          this.users = response.users;
        }
        this.loadingStates.users = true;
        this.checkLoadingComplete();
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

  private loadingStates = {
    users: false,
    vehicules: false,
    containers: false,
    routes: false,
    pickuppoints: false,
    incidents: false
  };

  private checkLoadingComplete(): void {
    const allLoaded = Object.values(this.loadingStates).every(state => state);
    if (allLoaded) {
      this.isLoading = false;
      this.initCharts();
    }
  }

  loadVehicules(): void {
    const sub = this.vehiculeService.getAll().subscribe({
      next: (response) => {
        if (response.vehicules) {
          this.vehicules = response.vehicules;
        }
        this.loadingStates.vehicules = true;
        this.checkLoadingComplete();
      },
      error: (error) => {
        console.error('Error loading vehicules:', error);
        this.loadingStates.vehicules = true;
        this.checkLoadingComplete();
      }
    });
    this.subscriptions.push(sub);
  }

  loadContainers(): void {
    const sub = this.containerService.getAll().subscribe({
      next: (response) => {
        if (response.containers) {
          this.containers = response.containers;
        }
        this.loadingStates.containers = true;
        this.checkLoadingComplete();
      },
      error: (error) => {
        console.error('Error loading containers:', error);
        this.loadingStates.containers = true;
        this.checkLoadingComplete();
      }
    });
    this.subscriptions.push(sub);
  }

  loadRoutes(): void {
    const sub = this.routeService.getAll().subscribe({
      next: (response) => {
        if (response.routes) {
          this.routes = response.routes;
        }
        this.loadingStates.routes = true;
        this.checkLoadingComplete();
      },
      error: (error) => {
        console.error('Error loading routes:', error);
        this.loadingStates.routes = true;
        this.checkLoadingComplete();
      }
    });
    this.subscriptions.push(sub);
  }

  loadPickUpPoints(): void {
    const sub = this.pickUpPointService.getAll().subscribe({
      next: (response: AppResponse) => {
        if (response.pickuppoints) {
          this.pickuppoints = response.pickuppoints;
        }
        this.loadingStates.pickuppoints = true;
        this.checkLoadingComplete();
      },
      error: (error) => {
        console.error('Error loading pickup points:', error);
        this.loadingStates.pickuppoints = true;
        this.checkLoadingComplete();
      }
    });
    this.subscriptions.push(sub);
  }

  loadIncidents(): void {
    const sub = this.incidentService.getAll().subscribe({
      next: (response: AppResponse) => {
        if (response.incidents) {
          this.incidents = response.incidents;
        }
        this.loadingStates.incidents = true;
        this.checkLoadingComplete();
      },
      error: (error) => {
        console.error('Error loading incidents:', error);
        this.loadingStates.incidents = true;
        this.checkLoadingComplete();
      }
    });
    this.subscriptions.push(sub);
  }

  // User Statistics
  get activeUsersCount(): number {
    return this.users.filter(u => u.isActive).length;
  }

  get adminUsersCount(): number {
    return this.users.filter(u => u.role === 'admin').length;
  }

  get employeeUsersCount(): number {
    return this.users.filter(u => u.role === 'employe').length;
  }

  // Vehicle Statistics
  get availableVehiculesCount(): number {
    return this.vehicules.filter(v => v.vehiculeStatus === 'functionel').length;
  }

  get maintenanceVehiculesCount(): number {
    return this.vehicules.filter(v => v.vehiculeStatus === 'non_functionel').length;
  }

  get vehiclesByType(): { [key: string]: number } {
    return this.vehicules.reduce((acc, v) => {
      // Handle undefined or null type values - check both possible property names
      const type = (v as any).vehiculeType || (v as any).type || 'Type Non Défini';
      // Clean up the type string
      const cleanType = type.toString().trim() || 'Type Non Défini';
      acc[cleanType] = (acc[cleanType] || 0) + 1;
      return acc;
    }, {} as { [key: string]: number });
  }

  // Container Statistics
  get functionalContainersCount(): number {
    return this.containers.filter(c => c.containerStatus === 'functionel').length;
  }

  get nonFunctionalContainersCount(): number {
    return this.containers.filter(c => c.containerStatus === 'non_functionel').length;
  }

  get containersByType(): { [key: string]: number } {
    return this.containers.reduce((acc, c) => {
      // Handle undefined or null type values
      const type = c.containerType || 'Unknown';
      acc[type] = (acc[type] || 0) + 1;
      return acc;
    }, {} as { [key: string]: number });
  }

  get containerFillLevels(): { low: number, medium: number, high: number, critical: number } {
    return this.containers.reduce((acc, c) => {
      const fillPercentage = (c.fillLevel / c.capacity) * 100;
      if (fillPercentage >= 90) acc.critical++;
      else if (fillPercentage >= 70) acc.high++;
      else if (fillPercentage >= 40) acc.medium++;
      else acc.low++;
      return acc;
    }, { low: 0, medium: 0, high: 0, critical: 0 });
  }
  //charts
  get containerStatusSeries(): number[] {
    const functional = this.functionalContainersCount;
    const nonFunctional = this.nonFunctionalContainersCount;
    return [functional, nonFunctional];
  }

  get containerStatusLabels(): string[] {
    return ['Functional', 'Non Functional'];
  }

  get containerTypeLabels(): string[] {
    const types = Object.keys(this.containersByType);
    if (types.length === 0) return ['No Data'];

    // Convert French enum values to English display names
    return types.map(type => {
      switch (type) {
        case 'Plastique': return 'Plastic';
        case 'Carton': return 'Cardboard';
        case 'Unknown': return 'Type Not Set';
        case 'undefined': return 'Type Not Set';
        default: return type;
      }
    });
  }

  get containerTypeSeries(): number[] {
    const values = Object.values(this.containersByType);
    return values.length > 0 ? values : [0];
  }

  get containerFillLevelLabels(): string[] {
    return ['Low', 'Medium', 'High', 'Critical'];
  }

  get containerFillLevelSeries(): number[] {
    const f = this.containerFillLevels;
    return [f.low, f.medium, f.high, f.critical];
  }

  // Vehicle chart data
  get vehicleStatusSeries(): number[] {
    const functional = this.availableVehiculesCount;
    const maintenance = this.maintenanceVehiculesCount;
    return [functional, maintenance];
  }

  get vehicleStatusLabels(): string[] {
    return ['Fonctionnel', 'En Maintenance'];
  }

  get vehicleTypeLabels(): string[] {
    const types = Object.keys(this.vehiclesByType);
    if (types.length === 0) {
      return ['Aucun Véhicule'];
    }
    return types;
  }

  get vehicleTypeSeries(): number[] {
    const values = Object.values(this.vehiclesByType);
    if (values.length === 0) {
      return [0];
    }
    return values;
  }

  // Pickup Point Statistics
  get pickuppointsWithContainers(): number {
    return this.pickuppoints.filter(p => p.containers && p.containers.length > 0).length;
  }

  get averageContainersPerPoint(): number {
    if (this.pickuppoints.length === 0) return 0;
    const totalContainers = this.pickuppoints.reduce((sum, p) => sum + (p.containers?.length || 0), 0);
    return Math.round(totalContainers / this.pickuppoints.length * 100) / 100;
  }

  // Route Statistics
  get todayRoutesCount(): number {
    const today = new Date().toISOString().split('T')[0];
    return this.routes.filter(r => r.routeDate.startsWith(today)).length;
  }

  get completedRoutesCount(): number {
    return this.routes.filter(r => r.status === 'completed').length;
  }

  get pendingRoutesCount(): number {
    return this.routes.filter(r => r.status === 'planned').length;
  }

  // Incident Statistics
  get reportedIncidentsCount(): number {
    return this.incidents.filter(i => i.status === 'reported').length;
  }

  get inProgressIncidentsCount(): number {
    return this.incidents.filter(i => i.status === 'in_progress').length;
  }

  get resolvedIncidentsCount(): number {
    return this.incidents.filter(i => i.status === 'resolved').length;
  }

  get incidentsByPriority(): { [key: string]: number } {
    return this.incidents.reduce((acc, i) => {
      acc[i.priority] = (acc[i.priority] || 0) + 1;
      return acc;
    }, {} as { [key: string]: number });
  }

  get highPriorityIncidentsCount(): number {
    return this.incidents.filter(i => i.priority === 'high').length;
  }

  get mediumPriorityIncidentsCount(): number {
    return this.incidents.filter(i => i.priority === 'medium').length;
  }

  get lowPriorityIncidentsCount(): number {
    return this.incidents.filter(i => i.priority === 'low').length;
  }

  get urgentIncidentsCount(): number {
    return this.incidents.filter(i => i.priority === 'urgent').length;
  }

  get incidentDangerPercentage(): number {
    if (this.incidents.length === 0) return 0;

    // Weight different incident types - considers both severity and volume
    const urgentWeight = 4;        // Highest weight - critical emergency
    const reportedWeight = 3;      // High weight - needs immediate attention
    const highPriorityWeight = 2;  // Important but being handled  
    const mediumPriorityWeight = 1; // Should be monitored
    const lowPriorityWeight = 0.3; // Still counts if many accumulate

    const weightedScore =
      (this.urgentIncidentsCount * urgentWeight) +
      (this.reportedIncidentsCount * reportedWeight) +
      (this.highPriorityIncidentsCount * highPriorityWeight) +
      (this.mediumPriorityIncidentsCount * mediumPriorityWeight) +
      (this.lowPriorityIncidentsCount * lowPriorityWeight);

    // Calculate percentage based on weighted maximum possible score
    const maxPossibleScore = this.incidents.length * urgentWeight;

    return Math.min(Math.round((weightedScore / maxPossibleScore) * 100), 100);
  }

  get incidentDangerLevel(): string {
    const percentage = this.incidentDangerPercentage;
    if (percentage >= 75) return 'CRITIQUE';
    if (percentage >= 50) return 'ÉLEVÉ';
    if (percentage >= 25) return 'MODÉRÉ';
    return 'FAIBLE';
  }

  get incidentDangerColor(): string {
    const percentage = this.incidentDangerPercentage;
    if (percentage >= 75) return 'text-danger';
    if (percentage >= 50) return 'text-warning';
    return 'text-success';
  }

  // Incident chart data
  get incidentStatusSeries(): number[] {
    const reported = this.reportedIncidentsCount;
    const inProgress = this.inProgressIncidentsCount;
    const resolved = this.resolvedIncidentsCount;
    return [reported, inProgress, resolved];
  }

  get incidentStatusLabels(): string[] {
    return ['Signalés', 'En Cours', 'Résolus'];
  }

  get incidentPrioritySeries(): number[] {
    const priorities = this.incidentsByPriority;
    return [
      priorities['HIGH'] || 0,
      priorities['MEDIUM'] || 0,
      priorities['LOW'] || 0
    ];
  }

  get incidentPriorityLabels(): string[] {
    return ['Haute Priorité', 'Priorité Moyenne', 'Basse Priorité'];
  }

  getRoleBadgeClass(role: string): string {
    return role === 'ADMIN' ? 'badge-admin' : 'badge-user';
  }

  getVehiculeStatusClass(status: string): string {
    return status === 'Fonctionnel' ? 'status-available' : 'status-maintenance';
  }

  getContainerStatusClass(status: string): string {
    return status === 'functional' ? 'status-functional' : 'status-non-functional';
  }

  setActiveSection(section: string): void {
    this.activeSection = section;
  }



  initCharts() {
    // Initialize charts even if some data is missing
    console.log('Initializing charts...');
    console.log('Containers:', this.containers.length);
    console.log('Vehicles:', this.vehicules.length);
    console.log('Incidents:', this.incidents.length);

    // Initialize incident charts first (priority chart with danger styling)
    if (this.incidents.length > 0) {
      // Gauge chart for incident danger level (weighted calculation considering volume + severity)
      const dangerPercentage = this.incidentDangerPercentage;

      this.incidentStatusChart = {
        series: [dangerPercentage],
        chart: {
          type: "radialBar",
          height: 350,
          offsetY: -20
        },
        plotOptions: {
          radialBar: {
            startAngle: -90,
            endAngle: 90,
            hollow: {
              margin: 15,
              size: "70%"
            },
            track: {
              background: '#e7e7e7',
              strokeWidth: '97%',
              margin: 15,
            },
            dataLabels: {
              name: {
                show: true,
                fontSize: '18px',
                fontWeight: 700,
                color: '#dc2626',
                offsetY: -10,
                formatter: () => 'NIVEAU DE DANGER'
              },
              value: {
                offsetY: 30,
                fontSize: '32px',
                fontWeight: 900,
                color: '#dc2626',
                formatter: (val: number) => {
                  if (val >= 70) return val + '%';
                  if (val >= 40) return val + '%';
                  return val + '%';
                }
              }
            }
          }
        },
        fill: {
          type: 'gradient',
          gradient: {
            shade: 'light',
            shadeIntensity: 0.4,
            inverseColors: false,
            opacityFrom: 1,
            opacityTo: 1,
            stops: [0, 50, 53, 91]
          }
        },
        colors: [dangerPercentage >= 75 ? '#dc2626' : dangerPercentage >= 50 ? '#f59e0b' : '#10b981'],
        labels: ['Incidents Critiques'],
        responsive: [{
          breakpoint: 480,
          options: {
            chart: {
              height: 300
            }
          }
        }]
      };
    } else {
      // Initialize empty incident chart
      this.incidentStatusChart = null;
    }

    // Debug logging for container types
    console.log('Container type data:');
    console.log('Total containers:', this.containers.length);
    console.log('Raw container data:', this.containers);
    console.log('Containers by type:', this.containersByType);
    console.log('Container type labels:', this.containerTypeLabels);
    console.log('Container type series:', this.containerTypeSeries);

    // Debug individual containers to see ALL properties
    this.containers.forEach((container, index) => {
      console.log(`Container ${index + 1} - ALL PROPERTIES:`, container);
      console.log(`Container ${index + 1} - SPECIFIC FIELDS:`, {
        id: container.id,
        containerType: (container as any).containerType,
        type: (container as any).type,
        capacity: container.capacity,
        fillLevel: container.fillLevel,
        status: container.containerStatus,
        allKeys: Object.keys(container)
      });
    });

    try {
      // Initialize container charts only if we have container data
      if (this.containers.length > 0) {
        // Radial Bar chart for container status (more modern visualization)
        this.containerStatusChart = {
          series: this.containerStatusSeries,
          chart: {
            type: "radialBar",
            height: 350
          },
          plotOptions: {
            radialBar: {
              dataLabels: {
                name: {
                  fontSize: '16px',
                  fontWeight: 600
                },
                value: {
                  fontSize: '20px',
                  fontWeight: 700
                },
                total: {
                  show: true,
                  label: 'Total',
                  formatter: () => {
                    return this.containers.length.toString();
                  }
                }
              }
            }
          },
          labels: this.containerStatusLabels,
          colors: ['#10b981', '#ef4444'],
          responsive: [{
            breakpoint: 480,
            options: {
              chart: {
                height: 300
              }
            }
          }]
        };

        // Horizontal Bar chart for container types (better for comparing categories)
        this.containerTypeChart = {
          series: [{
            name: 'Nombre de Conteneurs',
            data: this.containerTypeSeries
          }],
          chart: {
            type: "bar",
            height: 350,
            horizontal: true,
            toolbar: {
              show: false
            }
          },
          plotOptions: {
            bar: {
              borderRadius: 8,
              dataLabels: {
                position: 'right'
              }
            }
          },
          dataLabels: {
            enabled: true,
            formatter: (val: number) => val.toString()
          },
          xaxis: {
            categories: this.containerTypeLabels
          },
          colors: ['#3b82f6', '#f59e0b', '#6b7280', '#8b5cf6'],
          responsive: [{
            breakpoint: 480,
            options: {
              chart: {
                height: 300
              },
              plotOptions: {
                bar: {
                  horizontal: true
                }
              }
            }
          }]
        };

        // Area chart for fill levels (shows distribution better)
        this.containerFillChart = {
          series: [{
            name: 'Conteneurs',
            data: this.containerFillLevelSeries
          }],
          chart: {
            type: "area",
            height: 350,
            toolbar: {
              show: false
            },
            stacked: false
          },
          dataLabels: {
            enabled: true,
            formatter: (val: number) => val.toString()
          },
          stroke: {
            curve: 'smooth',
            width: 3
          },
          fill: {
            type: 'gradient',
            gradient: {
              shadeIntensity: 1,
              opacityFrom: 0.7,
              opacityTo: 0.3,
              stops: [0, 90, 100]
            }
          },
          xaxis: {
            categories: this.containerFillLevelLabels
          },
          colors: ['#10b981', '#f59e0b', '#ef4444', '#dc2626'],
          markers: {
            size: 6,
            strokeWidth: 2
          },
          responsive: [{
            breakpoint: 480,
            options: {
              chart: {
                height: 300
              }
            }
          }]
        };
      } else {
        // Initialize empty container charts
        this.containerStatusChart = null;
        this.containerTypeChart = null;
        this.containerFillChart = null;
      }

      // Always initialize vehicle charts
      // Debug vehicle data
      console.log('Vehicle data:');
      console.log('Total vehicles:', this.vehicules.length);
      console.log('Raw vehicle data:', this.vehicules);
      console.log('Available vehicles count:', this.availableVehiculesCount);
      console.log('Maintenance vehicles count:', this.maintenanceVehiculesCount);
      console.log('Vehicle by type:', this.vehiclesByType);
      console.log('Vehicle status series:', this.vehicleStatusSeries);
      console.log('Vehicle status labels:', this.vehicleStatusLabels);
      console.log('Vehicle type series:', this.vehicleTypeSeries);
      console.log('Vehicle type labels:', this.vehicleTypeLabels);

      // Debug individual vehicles to see ALL properties
      this.vehicules.forEach((vehicle, index) => {
        console.log(`Vehicle ${index + 1} - ALL PROPERTIES:`, vehicle);
        console.log(`Vehicle ${index + 1} - SPECIFIC FIELDS:`, {
          id: vehicle.id,
          matricul: vehicle.matricul,
          vehiculeType: vehicle.vehiculeType,
          vehiculeStatus: vehicle.vehiculeStatus,
          capacity: vehicle.capacity,
          allKeys: Object.keys(vehicle)
        });
      });

      // Gauge/Radial Bar chart for vehicle status (shows operational percentage)
      const totalVehicles = this.vehicules.length;
      const operationalPercentage = totalVehicles > 0
        ? Math.round((this.availableVehiculesCount / totalVehicles) * 100)
        : 0;

      this.vehicleStatusChart = {
        series: [operationalPercentage],
        chart: {
          type: "radialBar",
          height: 350
        },
        plotOptions: {
          radialBar: {
            hollow: {
              size: '60%'
            },
            dataLabels: {
              name: {
                fontSize: '18px',
                fontWeight: 600,
                color: '#1a202c'
              },
              value: {
                fontSize: '28px',
                fontWeight: 700,
                color: '#1a202c',
                formatter: (val: number) => val + '%'
              }
            },
            track: {
              background: '#e5e7eb'
            }
          }
        },
        labels: ['Opérationnels'],
        colors: ['#10b981'],
        responsive: [{
          breakpoint: 480,
          options: {
            chart: {
              height: 300
            }
          }
        }]
      };

      // Column chart for vehicle types (better for comparing quantities)
      const vehicleTypeSeriesData = this.vehicleTypeSeries;
      const vehicleTypeLabelsData = this.vehicleTypeLabels;

      console.log('Initializing vehicle type chart with:', {
        series: vehicleTypeSeriesData,
        labels: vehicleTypeLabelsData,
        vehiclesByType: this.vehiclesByType,
        totalVehicles: this.vehicules.length
      });

      // Ensure we always have valid data structure and arrays match
      let chartData = vehicleTypeSeriesData.length > 0 ? vehicleTypeSeriesData : [0];
      let chartLabels = vehicleTypeLabelsData.length > 0 ? vehicleTypeLabelsData : ['Aucun Véhicule'];

      // Ensure arrays have same length
      if (chartData.length !== chartLabels.length) {
        console.warn('Vehicle chart data mismatch:', { chartData, chartLabels });
        if (chartData.length > chartLabels.length) {
          chartData = chartData.slice(0, chartLabels.length);
        } else {
          chartLabels = chartLabels.slice(0, chartData.length);
        }
      }

      // Validate data contains only numbers
      chartData = chartData.map(val => typeof val === 'number' && !isNaN(val) ? val : 0);

      console.log('Final chart configuration:', {
        chartData,
        chartLabels,
        vehiclesByType: this.vehiclesByType,
        series: [{
          name: 'Nombre de Véhicules',
          data: chartData
        }]
      });

      this.vehicleTypeChart = {
        series: [{
          name: 'Nombre de Véhicules',
          data: chartData
        }],
        chart: {
          type: "bar",
          height: 350,
          toolbar: {
            show: false
          }
        },
        plotOptions: {
          bar: {
            borderRadius: 4,
            columnWidth: '60%',
            horizontal: false
          }
        },
        dataLabels: {
          enabled: true,
          formatter: (val: number) => val.toString()
        },
        xaxis: {
          categories: chartLabels,
          labels: {
            style: {
              fontSize: '12px'
            }
          }
        },
        yaxis: {
          title: {
            text: 'Nombre de Véhicules'
          }
        },
        colors: ['#3b82f6', '#f59e0b', '#ef4444', '#8b5cf6', '#10b981'],
        responsive: [{
          breakpoint: 480,
          options: {
            chart: {
              height: 300
            }
          }
        }]
      };

      console.log('Charts initialized successfully');
    } catch (error) {
      console.error('Error initializing charts:', error);
    }
  }

}