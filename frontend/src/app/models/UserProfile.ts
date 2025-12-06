
import { UserRole } from "./enums/UserRole";

export interface UserProfile {
    id: string;
    userName: string;
    email: string;
    firstName: string;
    isActive: boolean;
    lastName: string;
    role: UserRole;
    profileImage?: string;
    faceAuthEnabled?: boolean;
    token?: string;
    createdAt?: Date | string;
    updatedAt?: Date | string;

    // Employee-specific fields (optional)
    vehicules?: string[];
    competences?: string[];
    department?: string;
    shift?: string;
    driver?: boolean;
}