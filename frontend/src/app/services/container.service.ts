import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment-dev";
import { AppResponse } from "../models/AppResponse";
import { Container } from "../models/Container";
import { ContainerStatus } from "../models/enums/ContainerStatus";

@Injectable({
    providedIn: 'root'
})
export class ContainerService {
    URL = `${environment.API_URL}/public/containers`;

    constructor(private http: HttpClient) { }

    getAll(): Observable<AppResponse> {
        return this.http.get<AppResponse>(this.URL);
    }

    getById(id: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/${id}`);
    }

    getByPickUpPointId(pickUpPointId: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/pickuppoint/${pickUpPointId}`);
    }

    getByStatus(status: ContainerStatus): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/status/${status}`);
    }

    create(container: Partial<Container>): Observable<AppResponse> {
        return this.http.post<AppResponse>(this.URL, container);
    }

    update(id: string, container: Partial<Container>): Observable<AppResponse> {
        return this.http.put<AppResponse>(`${this.URL}/${id}`, container);
    }

    delete(id: string): Observable<AppResponse> {
        return this.http.delete<AppResponse>(`${this.URL}/${id}`);
    }
}
