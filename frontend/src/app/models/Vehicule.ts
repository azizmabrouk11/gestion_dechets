import { VehiculeType } from './enums/VehiculeType';
import { VehiculeStatus } from './enums/VehiculeStatus';
import { UserProfile } from './UserProfile';

export interface Vehicule {
    id?: string;
    _id?: string;
    matricul: string;
    capacity: number;
    vehiculeStatus: VehiculeStatus;
    vehiculeType: VehiculeType;
    users?: UserProfile[];
    createdAt?: string;
    updatedAt?: string;
}
