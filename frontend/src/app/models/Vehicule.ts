import { VehiculeType } from './enums/VehiculeType';
import { VehiculeStatus } from './enums/VehiculeStatus';

export interface Vehicule {
    id?: string;
    matricul: string;
    type: VehiculeType;
    capacity: number;
    status: VehiculeStatus;
    users?: string[];
    createdAt?: string;
    updatedAt?: string;
}
