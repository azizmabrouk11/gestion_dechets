import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { VehiculeService } from '../../../../services/vehicule.service';
import { UserService } from '../../../../services/user.service';
import { RouteService } from '../../../../services/route.service';
import { ToastService } from '../../../../services/toast.service';

@Component({
    selector: 'app-route-details',
    standalone: true,
    imports: [CommonModule, MatDialogModule, ReactiveFormsModule],
    templateUrl: './route-details.component.html',
    styleUrl: './route-details.component.css'
})
export class RouteDetailsComponent implements OnInit {
    formGroup!: FormGroup;
    isEditMode: boolean = false;
    isLoading: boolean = false;

    // Available options
    availableVehicles: any[] = [];
    availableUsers: any[] = [];
    filteredUsers: any[] = [];

    // Selected values
    selectedVehicle: any = null;
    selectedUsers: any[] = [];

    // Dropdown states
    showVehicleDropdown: boolean = false;
    showUsersDropdown: boolean = false;

    constructor(
        public dialogRef: MatDialogRef<RouteDetailsComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private formBuilder: FormBuilder,
        private vehiculeService: VehiculeService,
        private userService: UserService,
        private routeService: RouteService,
        private toastService: ToastService
    ) { }

    ngOnInit(): void {
        this.initializeForm();
        this.loadAvailableData();
    }

    initializeForm(): void {
        this.selectedVehicle = this.data.vehicule?.id || this.data.vehicule?._id ? this.data.vehicule : null;
        this.selectedUsers = this.data.users || [];

        this.formGroup = this.formBuilder.group({
            vehiculeId: [this.data.vehicule?.id || this.data.vehicule?._id || null],
            userIds: [this.data.users?.map((u: any) => u.id || u._id) || []]
        });
    }

    loadAvailableData(): void {
        // Load available vehicles
        this.vehiculeService.getAll().subscribe({
            next: (response: any) => {
                this.availableVehicles = response.vehicules || [];
            },
            error: (error: any) => {
                console.error('Error loading vehicles:', error);
                this.toastService.showError('Failed to load vehicles');
            }
        });

        // Load available users
        this.userService.getAll().subscribe({
            next: (response: any) => {
                this.availableUsers = response.users || [];
                // Initially show all users (will be filtered when vehicle is selected)
                this.filteredUsers = this.availableUsers;
            },
            error: (error: any) => {
                console.error('Error loading users:', error);
                this.toastService.showError('Failed to load users');
            }
        });
    }

    toggleEditMode(): void {
        this.isEditMode = !this.isEditMode;
    }

    toggleVehicleDropdown(): void {
        this.showVehicleDropdown = !this.showVehicleDropdown;
        if (this.showVehicleDropdown) this.showUsersDropdown = false;
    }

    toggleUsersDropdown(): void {
        this.showUsersDropdown = !this.showUsersDropdown;
        if (this.showUsersDropdown) this.showVehicleDropdown = false;
    }

    selectVehicle(vehicle: any): void {
        this.selectedVehicle = vehicle;
        this.formGroup.patchValue({ vehiculeId: vehicle.id || vehicle._id });
        this.showVehicleDropdown = false;

        // Filter users to show only drivers when a vehicle is selected
        this.filteredUsers = this.availableUsers.filter(user => user.driver === true);

        // Clear selected users that are not drivers
        this.selectedUsers = this.selectedUsers.filter(user => user.driver === true);
        this.formGroup.patchValue({ userIds: this.selectedUsers.map(u => u.id || u._id) });
    }

    toggleUserSelection(user: any): void {
        const userId = user.id || user._id;
        const index = this.selectedUsers.findIndex(u => (u.id || u._id) === userId);
        if (index > -1) {
            this.selectedUsers.splice(index, 1);
        } else {
            this.selectedUsers.push(user);
        }
        this.formGroup.patchValue({ userIds: this.selectedUsers.map(u => u.id || u._id) });
    }

    isUserSelected(user: any): boolean {
        const userId = user.id || user._id;
        return this.selectedUsers.some(u => (u.id || u._id) === userId);
    }

    removeUser(user: any): void {
        const userId = user.id || user._id;
        const index = this.selectedUsers.findIndex(u => (u.id || u._id) === userId);
        if (index > -1) {
            this.selectedUsers.splice(index, 1);
            this.formGroup.patchValue({ userIds: this.selectedUsers.map(u => u.id || u._id) });
        }
    }

    saveChanges(): void {
        if (this.formGroup.valid) {
            this.isLoading = true;
            const updateData: any = {};

            // Always send vehiculeId (can be null to clear, or an ID to set)
            const vehiculeId = this.formGroup.value.vehiculeId;
            updateData.vehiculeId = vehiculeId;

            // Always send userIds array (can be empty to clear, or have IDs to set)
            const userIds = this.formGroup.value.userIds || [];
            updateData.userIds = userIds;
            // Determine newly added users compared to original route data
            const prevUserIds = (this.data.users || []).map((u: any) => u.id || u._id);
            const newlyAddedUserIds = userIds.filter((id: string) => !prevUserIds.includes(id));

            console.log('Sending update data:', updateData);
            console.log('Selected vehicle:', this.selectedVehicle);
            console.log('Selected users:', this.selectedUsers);

            this.routeService.update(this.data.id, updateData).subscribe({
                next: (response: any) => {
                    this.isLoading = false;
                    this.toastService.showSuccess('Route updated successfully');
                    this.isEditMode = false;

                    // Update the local data with new values
                    if (response.route) {
                        this.data.vehicule = response.route.vehicule;
                        this.data.users = response.route.users;
                        this.selectedVehicle = response.route.vehicule;
                        this.selectedUsers = response.route.users || [];
                    }

                    // Close dropdown if open
                    this.showVehicleDropdown = false;
                    this.showUsersDropdown = false;
                    if (newlyAddedUserIds && newlyAddedUserIds.length > 0) {
                        this.toastService.showSuccess(`${newlyAddedUserIds.length} employee(s) notified by email`);
                    }
                },
                error: (error: any) => {
                    this.isLoading = false;
                    console.error('Error updating route:', error);
                    this.toastService.showError('Failed to update route');
                }
            });
        }
    }

    cancelEdit(): void {
        this.isEditMode = false;
        this.initializeForm();
        this.showVehicleDropdown = false;
        this.showUsersDropdown = false;
    }

    formatDistance(meters: number): string {
        if (meters >= 1000) {
            return (meters / 1000).toFixed(2) + ' km';
        }
        return meters.toFixed(0) + ' m';
    }

    formatTime(milliseconds: number): string {
        const minutes = Math.floor(milliseconds / 60000);
        const hours = Math.floor(minutes / 60);
        const remainingMinutes = minutes % 60;

        if (hours > 0) {
            return `${hours}h ${remainingMinutes}min`;
        }
        return `${minutes}min`;
    }

    getStatusBadgeClass(status: string): string {
        switch (status?.toLowerCase()) {
            case 'planned': return 'bg-blue-100 text-blue-800';
            case 'in_progress': return 'bg-yellow-100 text-yellow-800';
            case 'completed': return 'bg-green-100 text-green-800';
            case 'cancelled': return 'bg-red-100 text-red-800';
            default: return 'bg-gray-100 text-gray-800';
        }
    }

    getStatusLabel(status: string): string {
        switch (status?.toLowerCase()) {
            case 'planned': return 'Planned';
            case 'in_progress': return 'In Progress';
            case 'completed': return 'Completed';
            case 'cancelled': return 'Cancelled';
            default: return status;
        }
    }

    getFillPercentage(container: any): number {
        if (!container.capacity) return 0;
        return (container.fillLevel / container.capacity) * 100;
    }

    close(): void {
        this.dialogRef.close();
    }
}