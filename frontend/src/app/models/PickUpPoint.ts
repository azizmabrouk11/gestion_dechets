import { Container } from './Container';

export interface PickUpPoint {
    id?: string;
    locationLatitude: number;
    locationLongitude: number;
    containers?: Container[];
    address?:string;
}
