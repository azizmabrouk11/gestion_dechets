import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppResponse } from '../models/AppResponse';
import { Notification } from '../models/Notification';
import { environment } from '../../environments/environment-dev';

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private apiUrl =  environment.API_URL+'/public/notifications';

    constructor(private http: HttpClient) { }

    getAll(): Observable<AppResponse> {
        return this.http.get<AppResponse>(this.apiUrl);
    }

    getById(id: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.apiUrl}/${id}`);
    }

    getByUserId(userId: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.apiUrl}/user/${userId}`);
    }

    getByContainerId(containerId: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.apiUrl}/container/${containerId}`);
    }

    getByIncidentId(incidentId: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.apiUrl}/incident/${incidentId}`);
    }

    create(notification: Notification): Observable<AppResponse> {
        return this.http.post<AppResponse>(this.apiUrl, notification);
    }

    update(id: string, notification: Partial<Notification>): Observable<AppResponse> {
        return this.http.put<AppResponse>(`${this.apiUrl}/${id}`, notification);
    }

    delete(id: string): Observable<AppResponse> {
        return this.http.delete<AppResponse>(`${this.apiUrl}/${id}`);
    }
}
