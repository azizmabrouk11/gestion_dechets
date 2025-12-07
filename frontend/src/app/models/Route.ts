import { RouteStatus } from "./enums/RouteStatus";

export interface RouteInstruction {
    distance: number;
    sign: number;
    text: string;
    time: number;
    streetName?: string;
}

export interface Route {
    id?: string;
    pickUpPoints?: any[]; // Array of pickup point objects from backend
    pickUpPointIds?: string[];
    vehicule?: any; // Vehicle object from backend
    vehiculeId?: string;
    users?: any[]; // Array of user objects from backend
    userIds?: string[];
    routeDate: string;
    startTime?: string;
    endTime?: string;

    totalDistance?: number;
    totalTime?: number;
    encodedPolyline?: string;
    instructions?: RouteInstruction[];
    createdAt?: string;
    updatedAt?: string;
    status?: RouteStatus;
}
