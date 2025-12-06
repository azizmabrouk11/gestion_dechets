import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment-dev";
import { AppResponse } from "../models/AppResponse";
import { Vehicule } from "../models/Vehicule";

@Injectable({
    providedIn: 'root'
})
export class VehiculeService {
    URL = `${environment.API_URL}/public/vehicules`;

    constructor(private http: HttpClient) { }

    getAll(): Observable<AppResponse> {
        return this.http.get<AppResponse>(this.URL);
    }

    getById(id: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/${id}`);
    }

    getByMatricul(matricul: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/matricul/${matricul}`);
    }

    create(vehicule: Partial<Vehicule>): Observable<AppResponse> {
        console.log('Creating vehicule:', vehicule);
        return this.http.post<AppResponse>(this.URL, vehicule);
    }

    update(id: string, vehicule: Partial<Vehicule>): Observable<AppResponse> {
        return this.http.put<AppResponse>(`${this.URL}/${id}`, vehicule);
    }

    delete(id: string): Observable<AppResponse> {
        return this.http.delete<AppResponse>(`${this.URL}/${id}`);
    }
}
