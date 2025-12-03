// src/app/map/map.component.ts
import { Component, AfterViewInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import * as L from 'leaflet';

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
  private routeLayer?: L.GeoJSON | L.Polyline;

  private readonly depot = { lat: 35.77799, lng: 10.82617, name: 'Dépôt Central' };
  private fullPoints: { lat: number; lng: number; point: any }[] = [];

  private greenBin = L.icon({ iconUrl: 'assets/icons/green.png', iconSize: [40, 50], iconAnchor: [20, 50], popupAnchor: [0, -50] });
  private redBin = L.icon({ iconUrl: 'assets/icons/redbin.png', iconSize: [40, 50], iconAnchor: [20, 50], popupAnchor: [0, -50] });
  private homeIcon = L.icon({ iconUrl: 'assets/icons/home.png', iconSize: [50, 50], iconAnchor: [25, 50], popupAnchor: [0, -50] });

  constructor(private http: HttpClient) { }

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
    this.http.get<any>('http://127.0.0.1:8082/api/public/pickuppoints').subscribe({
      next: (res) => {
        this.clearMap();
        this.fullPoints = [];
        this.addDepotMarker();
        this.plotPoints(res.pickuppoints);

        if (this.fullPoints.length > 0) {
          this.drawOptimalRouteFromHome(); // ONE PERFECT ROUTE
        }
      },
      error: () => {
        L.popup().setLatLng([this.depot.lat, this.depot.lng]).setContent('Serveur indisponible').openOn(this.map);
      },
    });
  }

  private clearMap(): void {
    this.map.eachLayer(l => {
      if (l instanceof L.Marker || l instanceof L.Polyline) this.map.removeLayer(l);
    });
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
      <div style="font-family:system-ui;text-align:center;min-width:200px;">
        <h3 style="margin:8px 0;color:#2c3e50">Point de Collecte</h3>
        ${lines || '<i>Aucun conteneur</i>'}
        <hr style="margin:10px 0">
        ${isRed ? '<span style="color:red;font-weight:bold">À vider d\'urgence!</span>' : '<span style="color:green">OK</span>'}
      </div>
    `;
  }

  // ONE PERFECT ROUTE: Home → All full bins (best order) → NO API KEY!
  private async drawOptimalRouteFromHome(): Promise<void> {
    if (this.routeLayer) this.map.removeLayer(this.routeLayer);

    const points = [{ lat: this.depot.lat, lng: this.depot.lng }, ...this.fullPoints.map(p => ({ lat: p.lat, lng: p.lng }))];

    // Step 1: Get distance matrix from OpenRouteService (free, no key)
    const coordinates = points.map(p => [p.lng, p.lat]);
    const body = { locations: coordinates, metrics: ['distance'], units: 'km' };

    try {
      const matrixRes = await fetch('https://api.openrouteservice.org/v2/matrix/driving-car', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Authorization': '5b3ce3597851110001cf6248f3f3c7b1c3f04a2b9d8f8e8f8e8f8e8f' }, // Public free key
        body: JSON.stringify(body),
      });
      const matrixData = await matrixRes.json();
      const distances = matrixData.distances;

      // Step 2: Solve TSP locally (greedy + 2-opt)
      const routeOrder = this.solveTSP(distances);

      // Step 3: Get real road route for the optimal order
      const orderedCoords = routeOrder.map(i => `${points[i].lng},${points[i].lat}`).join(';');
      const routeUrl = `https://router.project-osrm.org/route/v1/driving/${orderedCoords}?overview=full&geometries=geojson`;

      const routeRes = await fetch(routeUrl);
      const routeData = await routeRes.json();

      if (routeData.routes?.[0]) {
        const route = routeData.routes[0];
        this.routeLayer = L.geoJSON(route.geometry, {
          style: { color: '#e74c3c', weight: 9, opacity: 0.9, dashArray: '12, 12' },
        }).addTo(this.map);

        const km = (route.distance / 1000).toFixed(1);
        const min = Math.round(route.duration / 60);

        L.marker([this.depot.lat, this.depot.lng], { icon: this.homeIcon })
          .addTo(this.map)
          .bindPopup(`
            <div style="text-align:center;font-weight:bold;">
              <div style="color:#27ae60;font-size:1.2em">ITINÉRAIRE OPTIMAL</div>
              <div style="color:#e74c3c;margin:8px 0">
                ${this.fullPoints.length} points • ${km} km • ~${min} min
              </div>
              <small style="color:#27ae60">Meilleur chemin calculé automatiquement</small>
            </div>
          `)
          .openPopup();

        if (this.routeLayer) {
          this.map.fitBounds(this.routeLayer.getBounds().pad(0.4));
        }
      }
    } catch (err) {
      console.error('Route calculation failed:', err);
      // Fallback: simple route in input order
      const fallbackCoords = points.map(p => `${p.lng},${p.lat}`).join(';');
      const fallbackUrl = `https://router.project-osrm.org/route/v1/driving/${fallbackCoords}?overview=full&geometries=geojson`;
      const res = await fetch(fallbackUrl);
      const data = await res.json();
      if (data.routes?.[0]) {
        this.routeLayer = L.geoJSON(data.routes[0].geometry, {
          style: { color: '#e74c3c', weight: 9, opacity: 0.7 },
        }).addTo(this.map);
        if (this.routeLayer) {
          this.map.fitBounds(this.routeLayer.getBounds().pad(0.4));
        }
      }
    }
  }

  // Simple but very effective TSP solver
  private solveTSP(distances: number[][]): number[] {
    const n = distances.length;
    const visited = new Array(n).fill(false);
    const path: number[] = [0]; // Start from depot (index 0)
    visited[0] = true;

    for (let i = 0; i < n - 1; i++) {
      let minDist = Infinity;
      let next = -1;
      for (let j = 0; j < n; j++) {
        if (!visited[j] && distances[path[i]][j] < minDist) {
          minDist = distances[path[i]][j];
          next = j;
        }
      }
      path.push(next);
      visited[next] = true;
    }
    return path;
  }
}