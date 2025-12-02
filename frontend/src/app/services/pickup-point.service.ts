import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment-dev";
import { AppResponse } from "../models/AppResponse";
import { PickUpPoint } from "../models/PickUpPoint";

@Injectable({
    providedIn: 'root'
})
export class PickUpPointService {
    URL = `${environment.API_URL}/pickuppoints`;

    constructor(private http: HttpClient) { }

    getAll(): Observable<AppResponse> {
        return this.http.get<AppResponse>(this.URL);
    }

    getById(id: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/${id}`);
    }

    getByLocation(location: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/location/${location}`);
    }

    create(pickUpPoint: Partial<PickUpPoint>): Observable<AppResponse> {
        return this.http.post<AppResponse>(this.URL, pickUpPoint);
    }

    update(id: string, pickUpPoint: Partial<PickUpPoint>): Observable<AppResponse> {
        return this.http.put<AppResponse>(`${this.URL}/${id}`, pickUpPoint);
    }

    delete(id: string): Observable<AppResponse> {
        return this.http.delete<AppResponse>(`${this.URL}/${id}`);
    }
}
