// src/app/map/map.component.ts  →  VERSION FINALE QUI MARCHE À 100%
import { Component, AfterViewInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { PickUpPointService } from '../../services/pickup-point.service';
import { RouteService } from '../../services/route.service';
import { Route } from '../../models/Route';
import { RouteStatus } from '../../models/enums/RouteStatus';
import * as L from 'leaflet';
import { AppResponse } from '../../models/AppResponse';
import { ToastService } from '../../services/toast.service';

delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'leaflet/dist/images/marker-icon-2x.png',
  iconUrl: 'leaflet/dist/images/marker-icon.png',
  shadowUrl: 'leaflet/dist/images/marker-shadow.png',
});

@Component({
  selector: 'app-map',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './map.component.html',
  styleUrl: './map.component.css',
})
export class MapComponent implements AfterViewInit {
  private map!: L.Map;
  private routeLayer: L.Polyline | null = null;
  optimizedRouteData: Route | null = null; // Store optimized route without saving
  isDuplicateRoute: boolean = false;
  duplicateCheckLoading: boolean = false;

  private readonly depot = { lat: 35.77799, lng: 10.82617, name: 'Dépôt Central - Monastir' };
  private fullPoints: { lat: number; lng: number; point: any }[] = [];

  private greenBin = L.icon({ iconUrl: 'assets/icons/green.png', iconSize: [40, 50], iconAnchor: [20, 50], popupAnchor: [0, -50] });
  private redBin = L.icon({ iconUrl: 'assets/icons/redbin.png', iconSize: [40, 50], iconAnchor: [20, 50], popupAnchor: [0, -50] });
  private homeIcon = L.icon({ iconUrl: 'assets/icons/home.png', iconSize: [50, 50], iconAnchor: [25, 50], popupAnchor: [0, -50] });

  private readonly GRAPHHOPPER_KEY = '0f520a1f-6282-4995-bb8f-d285c7cb0f11';

  constructor(
    private http: HttpClient,
    private pickUpPointService: PickUpPointService,
    private routeService: RouteService,
    private toastService: ToastService
  ) { }

  ngAfterViewInit(): void {
    this.initMap();
    this.loadPickupPoints();
    setInterval(() => this.loadPickupPoints(), 30000);
  }

  private initMap(): void {
    this.map = L.map('map', { center: [this.depot.lat, this.depot.lng], zoom: 13 });
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors',
    }).addTo(this.map);

    const legend = new L.Control({ position: 'bottomright' });
    legend.onAdd = () => {
      const div = L.DomUtil.create('div', 'leaflet-control-layers leaflet-control');
      div.style.cssText = 'background:white;padding:12px;border-radius:10px;box-shadow:0 0 15px rgba(0,0,0,0.3);font-family:system-ui;';
      div.innerHTML = `
        <strong>Légende</strong><br>
        <div><img src="assets/icons/home.png" width="28"> Dépôt</div>
        <div><img src="assets/icons/green.png" width="28"> OK</div>
        <div><img src="assets/icons/redbin.png" width="28"> Pleine / HS</div>
      `;
      return div;
    };
    legend.addTo(this.map);
  }

  private loadPickupPoints(): void {
    this.pickUpPointService.getAll().subscribe({
      next: (res: AppResponse) => {
        this.clearMap();
        this.fullPoints = [];
        this.addDepotMarker();
        this.plotPoints(res.pickuppoints || []);

        if (this.fullPoints.length > 0) {
          this.drawBestRouteFromHome();
        } else {
          this.map.setView([this.depot.lat, this.depot.lng], 13);
        }
      },
      error: () => L.popup().setLatLng([this.depot.lat, this.depot.lng]).setContent('Serveur indisponible').openOn(this.map),
    });
  }

  private clearMap(): void {
    this.map.eachLayer(l => { if (l instanceof L.Marker || l instanceof L.Polyline) this.map.removeLayer(l); });
    if (this.routeLayer) { this.map.removeLayer(this.routeLayer); this.routeLayer = null; }
    this.optimizedRouteData = null;
  }

  private addDepotMarker(): void {
    L.marker([this.depot.lat, this.depot.lng], { icon: this.homeIcon })
      .addTo(this.map)
      .bindPopup('<b style="color:#27ae60">Dépôt Central</b><br>Point de départ')
      .bindTooltip('DÉPART', { permanent: true, direction: 'top' });
  }

  private plotPoints(points: any[]): void {
    points.forEach(p => {
      const lat = p.locationLatitude;
      const lng = p.locationLongitude;
      const isFull = (p.containers || []).some((c: any) => c.fillLevel / c.capacity >= 0.8);
      const isBroken = (p.containers || []).some((c: any) => c.containerStatus === 'non_functional');
      const isRed = isFull || isBroken;

      if (isRed) this.fullPoints.push({ lat, lng, point: p });

      const icon = isRed ? this.redBin : this.greenBin;
      L.marker([lat, lng], { icon })
        .addTo(this.map)
        .bindPopup(this.createPopup(p, isRed))
        .bindTooltip(isRed ? 'Pleine!' : 'OK', { direction: 'top' });
    });
  }

  private createPopup(p: any, isRed: boolean): string {
    const lines = (p.containers || []).map((c: any) => {
      const pct = Math.round((c.fillLevel / c.capacity) * 100);
      const status = c.containerStatus === 'non_functional' ? 'HS' : `${pct}%`;
      return `<strong>${c.containerType}</strong>: ${status}`;
    }).join('<br>');

    return `
      <div style="font-family:system-ui;text-align:center;min-width:220px;">
        <h3 style="margin:8px 0;color:#2c3e50">Point de Collecte</h3>
        ${lines || '<i>Aucun conteneur</i>'}
        <hr style="margin:10px 0">
        ${isRed ? '<span style="color:red;font-weight:bold">À vider d\'urgence!</span>' : '<span style="color:green">OK</span>'}
      </div>
    `;
  }

  // VERSION QUI MARCHE À 100% – DÉCODE LE FORMAT ENCODÉ DE GRAPHHOPPER
  private async drawBestRouteFromHome(): Promise<void> {
    if (this.routeLayer) { this.map.removeLayer(this.routeLayer); this.routeLayer = null; }
    this.optimizedRouteData = null; // Reset when recalculating

    const points = [[this.depot.lng, this.depot.lat], ...this.fullPoints.map(p => [p.lng, p.lat] as [number, number])];

    try {
      const response = await fetch(`https://graphhopper.com/api/1/route?key=${this.GRAPHHOPPER_KEY}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ points, vehicle: 'car', optimize: true }),
      });

      const data = await response.json();

      if (data.paths?.[0]) {
        const path = data.paths[0];

        // DÉCODAGE DU POLYLINE ENCODÉ (c’est ça qui manquait !)
        const latlngs = this.decodePolyline(path.points);

        this.routeLayer = L.polyline(latlngs, {
          color: '#e74c3c',
          weight: 9,
          opacity: 0.9,
          dashArray: '15, 10',
        }).addTo(this.map);

        const km = (path.distance / 1000).toFixed(1);
        const min = Math.round(path.time / 60000);

        L.marker([this.depot.lat, this.depot.lng], { icon: this.homeIcon })
          .addTo(this.map)
          .bindPopup(`
            <div style="text-align:center;font-weight:bold;">
              <div style="color:#27ae60;font-size:1.3em">ITINÉRAIRE OPTIMAL</div>
              <div style="color:#e74c3c;margin:10px 0;font-size:1.1em">
                ${this.fullPoints.length} points • ${km} km • ~${min} min
              </div>
            </div>
          `)
          .openPopup();

        this.map.fitBounds(this.routeLayer.getBounds().pad(0.4));

        // Store optimized route data without saving to backend
        const pickupIds = this.fullPoints.map(p => {
          console.log('Processing pickup point:', p.point);
          const id = p.point._id || p.point.id;
          console.log('Extracted ID:', id);
          return id;
        });

        this.optimizedRouteData = {
          routeDate: new Date().toISOString(),
          totalDistance: path.distance,
          totalTime: path.time,
          encodedPolyline: path.points,
          pickUpPointIds: pickupIds,
          status: RouteStatus.planned,
          instructions: path.instructions.map((i: any) => ({
            distance: i.distance,
            sign: i.sign,
            text: i.text,
            time: i.time,
            streetName: i.street_name
          }))
        };

        // Check for duplicates
        this.duplicateCheckLoading = true;
        this.routeService.checkDuplicate(pickupIds).subscribe({
          next: (response) => {
            // 200 means duplicate found
            this.isDuplicateRoute = true;
            this.duplicateCheckLoading = false;
            this.toastService.showWarning('Warning: A route with these pickup points already exists!', 'warning');
          },
          error: (err) => {
            // 404 means not found (no duplicate), which is good
            if (err.status === 404) {
              this.isDuplicateRoute = false;
            } else {
              console.error('Error checking duplicate:', err);
            }
            this.duplicateCheckLoading = false;
          }
        });

        console.log('=== ROUTE OPTIMIZATION COMPLETE ===');
        console.log('Total pickup points:', this.optimizedRouteData.pickUpPointIds?.length);
        console.log('Pickup Point IDs being sent:', this.optimizedRouteData.pickUpPointIds);
        console.log('Full points array:', this.fullPoints);
        console.log('Ready to save. Click "Create Route" button.');
      }
    } catch (err) {
      console.error(err);
      alert('Erreur réseau temporaire');
    }
  }

  // Fonction magique qui décode le polyline encodé de GraphHopper
  private decodePolyline(encoded: string): L.LatLng[] {
    let index = 0, len = encoded.length;
    let lat = 0, lng = 0;
    const array: L.LatLng[] = [];

    while (index < len) {
      let b, shift = 0, result = 0;
      do {
        b = encoded.charCodeAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
      } while (b >= 0x20);
      const dlat = (result & 1) ? ~(result >> 1) : (result >> 1);
      lat += dlat;

      shift = 0;
      result = 0;
      do {
        b = encoded.charCodeAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
      } while (b >= 0x20);
      const dlng = (result & 1) ? ~(result >> 1) : (result >> 1);
      lng += dlng;

      array.push(L.latLng(lat * 1e-5, lng * 1e-5));
    }
    return array;
  }

  // Public method to save the optimized route
  public saveOptimizedRoute(): void {
    if (!this.optimizedRouteData) {
      this.toastService.showError('No optimized route found  to save');
      return;
    }

    console.log('=== SAVING ROUTE TO BACKEND ===');
    console.log('Route data:', JSON.stringify(this.optimizedRouteData, null, 2));
    console.log('Pickup Point IDs count:', this.optimizedRouteData.pickUpPointIds?.length);
    console.log('Pickup Point IDs:', this.optimizedRouteData.pickUpPointIds);

    this.routeService.create(this.optimizedRouteData).subscribe({
      next: (res) => {
        console.log('=== ROUTE SAVED SUCCESSFULLY ===');
        console.log('Backend response:', res);
        console.log('Pickup points sent:', this.optimizedRouteData?.pickUpPointIds?.length);
        console.log('Pickup points in response:', res.route?.pickUpPoints?.length);
        console.log('Response route:', res.route);
        this.optimizedRouteData = null; // Clear after saving
        this.toastService.showSuccess(`${res.message}`, 'Route Saved');
      },
      error: (err) => {
        console.error('=== ERROR SAVING ROUTE ===');
        console.error('Error:', err);
        console.error('Error response:', err.error);
        this.toastService.showError(`${err.error.message}`, 'Error saving route');

      }
    });
  }

  // Check if there's an optimized route ready to save
  public hasOptimizedRoute(): boolean {
    return this.optimizedRouteData !== null;
  }
}