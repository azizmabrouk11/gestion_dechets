import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment-dev";
import { AppResponse } from "../models/AppResponse";
import { Route } from "../models/Route";

@Injectable({
    providedIn: 'root'
})
export class RouteService {
    URL = `${environment.API_URL}/public/routes`;

    constructor(private http: HttpClient) { }

    getAll(): Observable<AppResponse> {
        return this.http.get<AppResponse>(this.URL);
    }

    getById(id: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/${id}`);
    }

    getByDate(date: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/date/${date}`);
    }

    getByDateRange(startDate: string, endDate: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/date-range?startDate=${startDate}&endDate=${endDate}`);
    }

    checkDuplicate(pickUpPointIds: string[]): Observable<AppResponse> {
        return this.http.post<AppResponse>(`${this.URL}/check-duplicate`, pickUpPointIds);
    }

    create(route: Partial<Route>): Observable<AppResponse> {
        return this.http.post<AppResponse>(this.URL, route);
    }

    update(id: string, route: Partial<Route>): Observable<AppResponse> {
        return this.http.put<AppResponse>(`${this.URL}/${id}`, route);
    }

    delete(id: string): Observable<AppResponse> {
        return this.http.delete<AppResponse>(`${this.URL}/${id}`);
    }
}
