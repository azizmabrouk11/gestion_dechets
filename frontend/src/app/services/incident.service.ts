import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment-dev";
import { AppResponse } from "../models/AppResponse";
import { Incident } from "../models/Incident";
import { IncidentStatus } from "../models/enums/IncidentStatus";
import { IncidentPriority } from "../models/enums/IncidentPriority";

@Injectable({
    providedIn: 'root'
})
export class IncidentService {
    URL = `${environment.API_URL}/public/incidents`;

    constructor(private http: HttpClient) { }

    getAll(): Observable<AppResponse> {
        return this.http.get<AppResponse>(this.URL);
    }

    getById(id: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/${id}`);
    }

    getByStatus(status: IncidentStatus): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/status/${status}`);
    }

    getByCitizenId(citizenId: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/citizen/${citizenId}`);
    }

    getByPickUpPointId(pickUpPointId: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/pickuppoint/${pickUpPointId}`);
    }

    getByPriority(priority: IncidentPriority): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/priority/${priority}`);
    }

    getByAssignedTo(assignedTo: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/assigned/${assignedTo}`);
    }

    create(incident: Partial<Incident>): Observable<AppResponse> {
        return this.http.post<AppResponse>(this.URL, incident);
    }

    update(id: string, incident: Partial<Incident>): Observable<AppResponse> {
        return this.http.put<AppResponse>(`${this.URL}/${id}`, incident);
    }

    delete(id: string): Observable<AppResponse> {
        return this.http.delete<AppResponse>(`${this.URL}/${id}`);
    }
}
