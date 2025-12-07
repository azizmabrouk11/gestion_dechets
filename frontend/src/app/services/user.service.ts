import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { UserProfile } from "../models/UserProfile";
import { environment } from "../../environments/environment-dev";
import { AppResponse } from "../models/AppResponse";
@Injectable({
    providedIn: 'root'
})
export class UserService {
    URL = `${environment.API_URL}/public/users`;
    constructor(private http: HttpClient) { }

    getAll(): Observable<AppResponse> {
        return this.http.get<AppResponse>(this.URL);

    }



    // add(userProfile: Partial<UserProfile>): Observable<UserProfile> {
    //     return this.http.post<UserProfile>(this.URL, userProfile);
    // }

    createOrUpdateUser(userProfile: Partial<UserProfile>): Observable<AppResponse> {
        return this.http.post<AppResponse>(`${this.URL}`, userProfile);
    }



    // updateUserProfile(id: any, profile: Partial<UserProfile>): Observable<Partial<UserProfile>> {
    //     return this.http.put<Partial<UserProfile>>(`${this.URL}/${id}`, profile);
    // }

    updateUserByUsername(username: string, profile: Partial<UserProfile>): Observable<AppResponse> {

        const updateData = {
            firstName: profile.firstName,
            lastName: profile.lastName,
            profileImage: profile.profileImage
        };
        return this.http.put<AppResponse>(`${this.URL}/username/${username}`, updateData);
    }

    updateUserByEmail(email: string, profile: Partial<UserProfile>): Observable<AppResponse> {
        const updateData = {
            firstName: profile.firstName,
            lastName: profile.lastName,
            profileImage: profile.profileImage
        };
        return this.http.put<AppResponse>(`${this.URL}/email/${email}`, updateData);
    }

    updateUserById(id: string, profile: Partial<UserProfile>): Observable<AppResponse> {
        return this.http.put<AppResponse>(`${this.URL}/${id}`, profile);
    }

    deleteUserById(id: string): Observable<AppResponse> {
        return this.http.delete<AppResponse>(`${this.URL}/${id}`);
    }

    getUserByUsername(username: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/username/${username}`);
    }

    getUserByEmail(email: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(`${this.URL}/email/${email}`);
    }

    registerFace(formData: FormData): Observable<AppResponse> {
        return this.http.post<AppResponse>(`${environment.API_URL}/face/register`, formData);
    }

    authenticateFace(formData: FormData): Observable<AppResponse> {
        return this.http.post<AppResponse>(`${environment.API_URL}/face/authenticate`, formData);
    }

    // getUserByUsername(username: string): Observable<UserProfile> {
    //     return this.http.get<UserProfile>(`${this.URL}/username/${username}`);
    // }
}