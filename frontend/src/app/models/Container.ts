import { ContainerType } from './enums/ContainerType';
import { ContainerStatus } from './enums/ContainerStatus';

export interface Container {
    id?: string;
    containerType: ContainerType;
    capacity: number;
    fillLevel: number;
    containerStatus: ContainerStatus;
    pickUpPointId: string;
    createdAt?: string;
    updatedAt?: string;
}
