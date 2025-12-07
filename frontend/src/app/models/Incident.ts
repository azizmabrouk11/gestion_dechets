import { IncidentStatus } from './enums/IncidentStatus';
import { IncidentPriority } from './enums/IncidentPriority';

export interface Incident {
    id?: string;
    description: string;
    location: string;
    status: IncidentStatus;
    priority: IncidentPriority;
    citizenId: string;
    pickUpPointId?: string;
    imageUrl?: string;
    date?: Date | string;
    resolvedAt?: string;
    assignedTo?: string;
    resolutionNotes?: string;
}

