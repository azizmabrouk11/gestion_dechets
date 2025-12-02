export interface CollectionRequest {
    id?: string;
    citizenId: string;
    pickUpPointId: string;
    description: string;
    priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
    status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
    createdAt?: string;
    completedAt?: string;
}
